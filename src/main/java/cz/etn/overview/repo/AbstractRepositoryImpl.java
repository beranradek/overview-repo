/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Filter;
import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.Pagination;
import cz.etn.overview.domain.Identifiable;
import cz.etn.overview.funs.CheckedFunction;
import cz.etn.overview.mapper.AttributeMapping;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.sql.DataSource;

/**
 * Basic abstract implementation of {@link AbstractRepository}.
 * @author Radek Beran
 */
public abstract class AbstractRepositoryImpl<T extends Identifiable<K>, K, F extends Filter> implements AbstractRepository<T, K, F> {
	
	protected static final String LIKE_WITH_PLACEHOLDER = "LIKE CONCAT('%', ?, '%')";
	
	// for inner implementation only
	private static class Pair<A, B> {
		A first;
		B second;
	}
	
	@Override
	public T create(T entity, boolean autogenerateKey) {
		Objects.requireNonNull(entity, "Entity should be specified");
		Pair<T, K> entityAndKeyAfterCommit = withNewConnection(conn -> {
			String tableName = getEntityMapper().getTableName();
			String attributeNamesCommaSeparated = getEntityMapper().getAttributeNamesCommaSeparated();
			String questionMarks = getEntityMapper().getPlaceholdersCommaSeparated();
			List<Object> attributeValues = getAttributeValues(entity);
			
			String sql = "INSERT INTO " + tableName + " (" + attributeNamesCommaSeparated + ") VALUES (" + questionMarks + ")";
			
			T createdEntity = entity;
			
			K generatedId = createInternal(conn, sql, attributeValues, autogenerateKey);
			
			Pair<T, K> p = new Pair<>();
			p.first = createdEntity;
			p.second = generatedId;
			return p;
		});
		T createdEntity = entityAndKeyAfterCommit.first;
		K generatedKey = entityAndKeyAfterCommit.second; 
		if (autogenerateKey) {
			createdEntity = entityUpdatedWithId(createdEntity, generatedKey);
		}
		return createdEntity;
	}
	
	@Override
	public Optional<T> update(T entity) {
		Objects.requireNonNull(entity, "Entity should be specified");
		return withNewConnection(conn -> {
			String tableName = getEntityMapper().getTableName();
			String attributeNamesEqToPlaceholders = getEntityMapper().getAttributeNamesEqToPlaceholdersCommaSeparated();
			List<Object> attributeValues = getAttributeValues(entity);
			String pkAttrName = getEntityMapper().getPrimaryAttributeName();
			Object pkAttrValue = getEntityMapper().getPrimaryAttributeValue(entity);
			
			String sql = "UPDATE " + tableName + " SET " + attributeNamesEqToPlaceholders + " WHERE " + pkAttrName + "=?";
			final List<Object> parameterValues = new ArrayList<>();
			parameterValues.addAll(attributeValues);
			parameterValues.add(pkAttrValue);
			
			int updatedCount = updateInternal(conn, sql, parameterValues);
			if (updatedCount == 1) {
				return Optional.<T>of(entity);
			}
			return Optional.<T>empty();
		});
	}

	@Override
	public boolean delete(K id) {
		Objects.requireNonNull(id, "id should be specified");
		return withNewConnection(conn -> {
			String tableName = getEntityMapper().getTableName();
			String idFieldName = getEntityMapper().getPrimaryAttributeName();
			
			String sql = "DELETE FROM " + tableName + " WHERE " + idFieldName + "=?";
			final List<Object> parameterValues = new ArrayList<>();
			parameterValues.add(id);
			
			int updatedCount = updateInternal(conn, sql, parameterValues);
			return updatedCount == 1;
		});
	}
	
	@Override
	public Optional<T> findById(K id) {
		return findByAttributeValue(getEntityMapper().getPrimaryAttributeName(), id);
	}
	
	@Override
	public List<T> findByOverviewSettings(final Overview<F> overview) {
		List<String> attributeNames = getEntityMapper().getAttributeNames();
		String from = getEntityMapper().getTableName();
		return findByOverviewSettingsInternal(overview, attributeNames, from, rs -> getEntityMapper().buildEntity(rs));
	}
	
	@Override
	public int countByOverviewSettings(final Overview<F> overview) {
		String selection = "COUNT(*)";
		String from = getEntityMapper().getTableName();
		return countByOverviewSettingsInternal(overview, selection, from);
	}
	
	/**
	 * Should be overriden in subclasses to apply filtering set in input filter. 
	 * @param filter
	 * @return
	 */
	protected List<FilterCondition> composeFilterConditions(F filter) {
		return new ArrayList<>();
	}
	
	/**
	 * @param overview
	 * @param selectedAttributes
	 * @param from
	 * @param entityBuilder
	 * @return
	 */
	protected List<T> findByOverviewSettingsInternal(final Overview<F> overview, List<String> selectedAttributes, String from, Function<AttributeSource, T> entityBuilder) {
		Objects.requireNonNull(overview, "overview should be specified");
		return withNewConnection(conn -> {
			
			final List<Order> ordering = (overview.getOrder() == null || overview.getOrder().isEmpty()) ? createDefaultOrdering() : overview.getOrder();
			
			return queryWithOverview(conn, 
				selectedAttributes, 
				from,
				() -> composeFilterConditions(overview.getFilter()),
				() -> ordering,
				() -> overview.getPagination(),
				entityBuilder);
		});
	}
	
	/**
	 * @param overview
	 * @param selection
	 * @param from
	 * @return
	 */
	protected int countByOverviewSettingsInternal(final Overview<F> overview, String selection, String from) {
		Objects.requireNonNull(overview, "overview should be specified");
		return withNewConnection(conn -> {
			return queryCount(conn, selection, from, () -> composeFilterConditions(overview.getFilter()));
		});
	}
	
	protected <U> U withNewConnection(CheckedFunction<Connection, U> queryData) {
		try (Connection conn = getDataSource().getConnection()) {
			U result = queryData.apply(conn);
			handleCommit(conn);
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	protected void handleCommit(Connection conn) {
		try {
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	protected <U> Optional<T> findByAttributeValue(String attrName, U attrValue) {
		Objects.requireNonNull(attrValue, attrName + " value should be specified");
		return withNewConnection(conn -> {
			List<String> attributeNames = getEntityMapper().getAttributeNames();
			String from = getEntityMapper().getTableName();
			
			List<Object> attributeValues = new ArrayList<>();
			attributeValues.add(attrValue);
			
			List<FilterCondition> filterConditions = new ArrayList<>();
			filterConditions.add(new FilterCondition(attrName + "=?", attributeValues));
			
			List<T> results = queryWithOverview(conn, 
				attributeNames, 
				from,
				() -> filterConditions,
				() -> null,
				() -> null,
				rs -> getEntityMapper().buildEntity(rs));
			return results != null && !results.isEmpty() ? Optional.<T>ofNullable(results.get(0)) : Optional.<T>empty();
		});
	}
	
	protected abstract DataSource getDataSource();
	
	protected abstract EntityMapper<T> getEntityMapper();
	
	/**
	 * It is recommended that subclasses provide more effective implementation. 
	 * @param entity
	 * @param id
	 * @return entity updated with given id
	 */
	protected T entityUpdatedWithId(T entity, K id) {
		return findById(id).get();
	}
	
	/**
	 * Subclasses should override this when the type of key is not compatible with an Integer.
	 * @param generatedId
	 * @return
	 */
	protected K convertGeneratedKey(Integer generatedId) {
		return (K)generatedId;
	}
	
	protected Date instantToUtilDate(Instant date) {
		if (date == null) return null;
		return new Date(date.toEpochMilli());
	}
	
	protected <U> String dbAttributeWithPrefix(String prefix, AttributeMapping<U> attr) {
		return prefix + "." + attr.getAttributeName();
	}

	protected List<Order> createDefaultOrdering() {
		// default ordering by id
		List<Order> ordering = new ArrayList<>();
		ordering.add(new Order(getEntityMapper().getTableName() + "." + getEntityMapper().getPrimaryAttributeName(), false));
		return ordering;
	}
	
	protected abstract K createInternal(Connection conn, String sql, List<Object> attributeValues, boolean autogenerateKey);
	
	protected abstract int updateInternal(Connection conn, String sql, List<Object> attributeValues);
	
	protected abstract List<T> queryWithOverview(Connection conn,
		List<String> selectedAttributes,	
		String from,
		Supplier<List<FilterCondition>> filterConditionsSupplier, 
		Supplier<List<Order>> orderingSupplier, 
		Supplier<Pagination> paginationSupplier,
		Function<AttributeSource, T> entityBuilder);
	
	protected abstract int queryCount(Connection conn, String selection, String from, Supplier<List<FilterCondition>> filterSupplier);
	
	protected List<Object> getAttributeValues(T entity) {
		List<Object> attributeValues = getEntityMapper().getAttributeValues(entity);
		List<Object> result = new ArrayList<>();
		if (attributeValues != null) {
			for (Object v : attributeValues) {
				if (v instanceof Instant) {
					result.add(instantToUtilDate((Instant)v));
				} else {
					result.add(v);
				}
			}
		}
		return result;
	}
}
