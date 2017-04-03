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
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.mapper.ResultSetAttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Basic abstract implementation of {@link Repository}.
 * @author Radek Beran
 */
public abstract class AbstractRepository<T extends Identifiable<K>, K, F extends Filter> implements Repository<T, K, F> {

	protected static final Logger log = LoggerFactory.getLogger(AbstractRepository.class);

	// for inner implementation only
	private static class Pair<A, B> {
		A first;
		B second;
	}
	
	@Override
	public T create(T entity, boolean autogenerateKey) {
		Objects.requireNonNull(entity, "Entity should be specified");
		Pair<T, K> entityAndKeyAfterCommit = withNewConnection(conn -> {
			String tableName = getEntityMapper().getDataSet();
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
			String tableName = getEntityMapper().getDataSet();
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
			String tableName = getEntityMapper().getDataSet();
			String idFieldName = getEntityMapper().getPrimaryAttributeName();
			
			String sql = "DELETE FROM " + tableName + " WHERE " + idFieldName + "=?";
			final List<Object> parameterValues = new ArrayList<>();
			parameterValues.add(id);
			
			int updatedCount = updateInternal(conn, sql, parameterValues);
			return updatedCount == 1;
		});
	}

	@Override
	public int deleteByFilter(F filter) {
		Objects.requireNonNull(filter, "filter should be specified");
		return withNewConnection(conn -> {
			String from = getEntityMapper().getDataSet();
			StringBuilder sqlBuilder = new StringBuilder("DELETE FROM " + from);
			List<Object> parameters = appendFilter(sqlBuilder, composeFilterConditions(filter));
			return updateInternal(conn, sqlBuilder.toString(), parameters);
		});
	}

	@Override
	public int countByFilter(F filter) {
		String selection = "COUNT(*)";
		String from = getEntityMapper().getDataSet();
		return countByFilterInternal(filter, selection, from);
	}

	@Override
	public Optional<T> findById(K id) {
		return findByAttributeValue(getEntityMapper().getPrimaryAttributeName(), id);
	}
	
	@Override
	public List<T> findByOverview(final Overview<F> overview) {
		List<String> attributeNames = getEntityMapper().getAttributeNames();
		String from = getEntityMapper().getDataSet();
		return findByOverviewInternal(overview, attributeNames, from, rs -> getEntityMapper().buildEntity(rs));
	}

	protected abstract DataSource getDataSource();

	protected abstract EntityMapper<T> getEntityMapper();

	/**
	 * Should be overriden in subclasses to apply filtering updatedEntity in input filter.
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
	protected List<T> findByOverviewInternal(final Overview<F> overview, List<String> selectedAttributes, String from, Function<AttributeSource, T> entityBuilder) {
		Objects.requireNonNull(overview, "overview should be specified");
		return withNewConnection(conn -> {
			
			final List<Order> ordering = (overview.getOrder() == null || overview.getOrder().isEmpty()) ? createDefaultOrdering() : overview.getOrder();
			
			return queryWithOverview(conn, 
				selectedAttributes, 
				from,
				overview.getFilter(),
				ordering,
				overview.getPagination(),
				entityBuilder);
		});
	}
	
	/**
	 * @param filter
	 * @param selection
	 * @param from
	 * @return
	 */
	protected int countByFilterInternal(final F filter, String selection, String from) {
		return withNewConnection(conn -> queryCount(conn, selection, from, filter));
	}
	
	protected <U> U withNewConnection(CheckedFunction<Connection, U> queryData) {
		Connection conn = null;
		boolean success = false;
		U result = null;
		try {
			conn = getDataSource().getConnection();
			result = queryData.apply(conn);
			success = true;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		} finally {
			if (conn != null) {
				try {
					if (!conn.getAutoCommit()) {
						if (success) {
							conn.commit();
						} else {
							conn.rollback();
						}
					}
				} catch (SQLException ex) {
					throw new RuntimeException(ex.getMessage(), ex);
				} finally {
					try {
						conn.close();
					} catch (SQLException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}
		}
		return result;
	}
	
	protected <U> Optional<T> findByAttributeValue(String attrName, U attrValue) {
		Objects.requireNonNull(attrValue, attrName + " value should be specified");
		return withNewConnection(conn -> {
			List<String> attributeNames = getEntityMapper().getAttributeNames();
			String from = getEntityMapper().getDataSet();
			
			List<Object> attributeValues = new ArrayList<>();
			attributeValues.add(attrValue);
			
			List<FilterCondition> filterConditions = new ArrayList<>();
			filterConditions.add(new FilterCondition(attrName + "=?", attributeValues));
			
			List<T> results = queryWithOverview(conn, 
				attributeNames, 
				from,
				filterConditions,
				null,
				null,
				rs -> getEntityMapper().buildEntity(rs));
			return results != null && !results.isEmpty() ? Optional.<T>ofNullable(results.get(0)) : Optional.<T>empty();
		});
	}
	
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

	protected List<Order> createDefaultOrdering() {
		// default ordering by id
		List<Order> ordering = new ArrayList<>();
		ordering.add(new Order(getEntityMapper().getDataSet() + "." + getEntityMapper().getPrimaryAttributeName(), false));
		return ordering;
	}
	
	protected K createInternal(Connection conn, String sql, List<Object> attributeValues, boolean autogenerateKey) {
		K generatedId = null;
		try {
			try (PreparedStatement statement = conn.prepareStatement(sql, autogenerateKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
				setParameters(statement, attributeValues);
				if (autogenerateKey) {
					statement.executeUpdate();
					ResultSet rs = statement.getGeneratedKeys();
					rs.next();
					generatedId = convertGeneratedKey(rs.getInt(1));
				} else {
					statement.executeUpdate();
				}
				logSqlWithParameters(statement.toString(), attributeValues);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return generatedId;
	}
	
	protected int updateInternal(Connection conn, String sql, List<Object> attributeValues) {
		try {
			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameters(statement, attributeValues);
				int updatedCount = statement.executeUpdate();
				logSqlWithParameters(statement.toString(), attributeValues);
				return updatedCount;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	protected List<T> queryWithOverview(Connection conn,
		List<String> selectedAttributes,
		String from,
		F filter,
		List<Order> ordering,
		Pagination pagination,
		Function<AttributeSource, T> entityBuilder) {

		return queryWithOverview(conn,
			selectedAttributes,
			from,
			filter != null ? composeFilterConditions(filter) : null,
			ordering,
			pagination,
			entityBuilder);
	}
	
	protected List<T> queryWithOverview(Connection conn,
		List<String> selectedAttributes,	
		String from,
		List<FilterCondition> filterConditions,
		List<Order> ordering,
		Pagination pagination,
		Function<AttributeSource, T> entityBuilder) {

		List<T> results = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder("SELECT " + CollectionFuns.join(selectedAttributes, ", ") + " FROM " + from);
			List<Object> parameters = appendFilter(sqlBuilder, filterConditions);
			appendOrdering(sqlBuilder, ordering);
			appendPagination(sqlBuilder, pagination);

			String sql = sqlBuilder.toString();

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameters(statement, parameters);

				try (ResultSet rs = statement.executeQuery()) {
					results = new ArrayList<>();
					while (rs.next()) {
						results.add(entityBuilder.apply(new ResultSetAttributeSource(rs)));
					}
				}
			}

			logSqlWithParameters(sql, parameters);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		return results;
	}

	protected int queryCount(Connection conn, String selection, String from, F filter) {
		int count = 0;
		try {
			StringBuilder sqlBuilder = new StringBuilder("SELECT " + selection + " FROM " + from);
			List<Object> parameters = appendFilter(sqlBuilder, filter != null ? composeFilterConditions(filter) : null);

			String sql = sqlBuilder.toString();

			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameters(statement, parameters);

				try (ResultSet rs = statement.executeQuery()) {
					if (rs.next()) {
						count = rs.getInt(1);
					}
				}
			}

			logSqlWithParameters(sql, parameters);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		return count;
	}

	protected List<Object> appendFilter(StringBuilder sqlBuilder, List<FilterCondition> filterConditions) {
		List<Object> parameters = null;
		if (filterConditions != null && !filterConditions.isEmpty()) {
			List<String> whereClause = filterConditions.stream().map(c -> c.getConditionWithPlaceholders()).collect(Collectors.toList());
			parameters = filterConditions.stream().flatMap(c -> c.getValues().stream()).collect(Collectors.toList());
			sqlBuilder.append(" WHERE ").append(CollectionFuns.join(whereClause, " AND "));
		}
		return parameters;
	}

	protected void appendOrdering(StringBuilder sqlBuilder, List<Order> ordering) {
		if (ordering != null && !ordering.isEmpty()) {
			List<String> orderByClause = ordering.stream().map(c -> c.getDbString()).collect(Collectors.toList());
			sqlBuilder.append(" ORDER BY ").append(CollectionFuns.join(orderByClause, ", "));
		}
	}

	protected void appendPagination(StringBuilder sqlBuilder, Pagination pagination) {
		if (pagination != null) {
			// TODO RBe: Abstraction over different databases?
			sqlBuilder.append(" LIMIT " + pagination.getLimit() + " OFFSET " + pagination.getOffset());
		}
	}

	protected void setParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
		if (parameters != null) {
            int i = 0;
            for (Object paramValue : parameters) {
                statement.setObject(i + 1, paramValue);
                i++;
            }
        }
	}

	protected void logSqlWithParameters(String sql, List<Object> parameters) {
		if (log.isTraceEnabled()) {
            log.trace(sql.toString());
            logParameters(parameters);
        }
	}

	protected void logParameters(List<Object> parameters) {
		if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                Object p = parameters.get(i);
                log.trace("{}: {}", i + 1, p);
            }
        }
	}

	protected List<Object> getAttributeValues(T entity) {
		List<Object> attributeValues = getEntityMapper().getAttributeValues(entity);
		List<Object> result = new ArrayList<>();
		if (attributeValues != null) {
			for (Object v : attributeValues) {
				// TODO RBe: Conversion of another data types when not supported their passing to JDBC?
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
