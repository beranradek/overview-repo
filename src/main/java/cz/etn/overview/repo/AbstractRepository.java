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
			
			K generatedId = create(conn, sql, attributeValues, autogenerateKey);
			
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
			String attributeNamesEqToPlaceholders = getEntityMapper().getAttributeNamesEqToPlaceholdersCommaSeparated();

			StringBuilder sqlBuilder = new StringBuilder("UPDATE " + getEntityMapper().getDataSet() + " SET " + attributeNamesEqToPlaceholders);
			List<Object> primaryKeyParameters = appendFilter(sqlBuilder, composeFilterConditionsForPrimaryKey(entity));
			String sql = sqlBuilder.toString();

			final List<Object> parameterValues = new ArrayList<>();
			parameterValues.addAll(getAttributeValues(entity));
			parameterValues.addAll(primaryKeyParameters);
			
			int updatedCount = update(conn, sql, parameterValues);
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
			StringBuilder sqlBuilder = new StringBuilder("DELETE FROM " + getEntityMapper().getDataSet());
			List<Object> primaryKeyParameters = appendFilter(sqlBuilder, composeFilterConditionsForPrimaryKey(id));
			String sql = sqlBuilder.toString();
			
			int updatedCount = update(conn, sql, primaryKeyParameters);
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
			return update(conn, sqlBuilder.toString(), parameters);
		});
	}

    /**
     * Returns aggregated values of given attribute for given filter.
     * @param aggType aggregation type
     * @param resultClass
     * @param attrName
     * @param filter
     * @param from
     * @param <R>
     * @return
     */
    protected <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter, String from) {
        Objects.requireNonNull(aggType, "aggregation type should be specified");
        Objects.requireNonNull(resultClass, "result class should be specified");
        Objects.requireNonNull(attrName, "attribute name should be specified");

        return withNewConnection(conn -> {
            String aggAttributeAlias = attrName + "_agg";
            List<R> results = queryWithOverview(conn,
                    aggFunction(aggType, attrName) + " AS " + aggAttributeAlias,
                    from,
                    composeFilterConditions(filter),
                    null,
                    null,
                    as -> as.get(resultClass, aggAttributeAlias));
            return results != null && !results.isEmpty() ? results.get(0) : null;
        });
    }

	@Override
	public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter) {
		return aggByFilter(aggType, resultClass, attrName, filter, getEntityMapper().getDataSet());
	}

	@Override
	public Optional<T> findById(K id) {
		return CollectionFuns.headOpt(findByFilterConditions(composeFilterConditionsForPrimaryKey(id), null));
	}
	
	@Override
	public List<T> findByOverview(final Overview<F> overview) {
		List<String> attributeNames = getEntityMapper().getAttributeNames();
		String from = getEntityMapper().getDataSet();
		return findByOverview(overview, attributeNames, from, rs -> getEntityMapper().buildEntity(rs));
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
	 * Composes filter conditions to match primary key attributes.
	 * @param id
	 * @return
	 */
	protected List<FilterCondition> composeFilterConditionsForPrimaryKey(K id) {
		List<FilterCondition> conditions = new ArrayList<>();
		List<String> names = getEntityMapper().getPrimaryAttributeNames();
		if (names.size() == 1) {
			conditions.add(FilterCondition.eq(names.get(0), id));
		} else {
			// composed primary key
			conditions.addAll(composeFilterConditionsForCompositePrimaryKey(id));
		}
		return conditions;
	}

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param entity
	 * @return
	 */
	protected List<FilterCondition> composeFilterConditionsForPrimaryKey(T entity) {
		List<FilterCondition> conditions = new ArrayList<>();
		List<String> names = getEntityMapper().getPrimaryAttributeNames();
		List<Object> values = getEntityMapper().getPrimaryAttributeValues(entity);
		for (int i = 0; i < names.size(); i++) {
			String attrName = names.get(i);
			Object attrValue = values.get(i);
			conditions.add(FilterCondition.eq(attrName, attrValue));
		}
		return conditions;
	}

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param id
	 * @return
	 */
	protected List<FilterCondition> composeFilterConditionsForCompositePrimaryKey(K id) {
		throw new UnsupportedOperationException("Decomposition of composite key to filter conditions is not implemented. Key: " + id);
	}

	protected List<Order> composeOrderingForPrimaryKey() {
		List<Order> ordering = new ArrayList<>();
		List<String> names = getEntityMapper().getPrimaryAttributeNames();
		if (names != null) {
			for (String name : names) {
				String tablePrefix = getEntityMapper().getDataSet() + ".";
				ordering.add(new Order(name.startsWith(tablePrefix) ? name : tablePrefix + name, false));
			}
		}
		return ordering;
	}
	
	/**
	 * @param overview
	 * @param selectedAttributes
	 * @param from
	 * @param entityBuilder
	 * @return
	 */
	protected List<T> findByOverview(final Overview<F> overview, List<String> selectedAttributes, String from, Function<AttributeSource, T> entityBuilder) {
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

	protected List<T> findByFilterConditions(List<FilterCondition> filterConditions, List<Order> ordering) {
		return withNewConnection(conn -> {
			return queryWithOverview(conn,
					getEntityMapper().getAttributeNames(),
					getEntityMapper().getDataSet(),
					filterConditions,
					ordering,
					null,
					as -> getEntityMapper().buildEntity(as));
		});
	}

	/**
	 * Returns one entity for given unique attribute name and value.
	 * @param attrName
	 * @param attrValue
	 * @param <U>
	 * @return
	 */
	protected <U> Optional<T> findByAttributeValue(String attrName, U attrValue) {
		Objects.requireNonNull(attrValue, attrName + " value should be specified");
		List<FilterCondition> conditions = new ArrayList<>();
		conditions.add(FilterCondition.eq(attrName, attrValue));
		return CollectionFuns.headOpt(findByFilterConditions(conditions, createDefaultOrdering()));
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
		return composeOrderingForPrimaryKey();
	}
	
	protected K create(Connection conn, String sql, List<Object> attributeValues, boolean autogenerateKey) {
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
	
	protected int update(Connection conn, String sql, List<Object> attributeValues) {
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

	// Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
	protected <T> List<T> queryWithOverview(Connection conn,
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

	// Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
	protected <T> List<T> queryWithOverview(Connection conn,
		List<String> selectedAttributes,
		String from,
		List<FilterCondition> filterConditions,
		List<Order> ordering,
		Pagination pagination,
		Function<AttributeSource, T> entityBuilder) {

		return queryWithOverview(conn,
			CollectionFuns.join(selectedAttributes, ", "),
			from,
			filterConditions,
			ordering,
			pagination,
			entityBuilder
		);
	}

	// Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
	protected <T> List<T> queryWithOverview(Connection conn,
		String selection,
		String from,
		List<FilterCondition> filterConditions,
		List<Order> ordering,
		Pagination pagination,
		Function<AttributeSource, T> entityBuilder) {

		List<T> results = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder("SELECT " + selection + " FROM " + from);
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

	protected String aggFunction(AggType aggType, String attrName) {
		String fun;
		switch (aggType) {
			case COUNT:
				fun = "COUNT(" + attrName + ")";
				break;
			case SUM:
				fun = "SUM(" + attrName + ")";
				break;
			case MIN:
				fun = "MIN(" + attrName + ")";
				break;
			case MAX:
				fun = "MAX(" + attrName + ")";
				break;
			case AVG:
				fun = "AVG(" + attrName + ")";
				break;
			default:
				throw new IllegalArgumentException("Unsupported aggregation type: " + aggType);
		}
		return fun;
	}
}
