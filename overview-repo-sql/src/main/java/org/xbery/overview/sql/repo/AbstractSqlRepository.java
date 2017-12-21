/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbery.overview.sql.repo;

import org.xbery.overview.*;
import org.xbery.overview.common.Pair;
import org.xbery.overview.common.funs.CheckedFunction;
import org.xbery.overview.common.funs.CollectionFuns;
import org.xbery.overview.filter.Condition;
import org.xbery.overview.filter.EqAttributesCondition;
import org.xbery.overview.mapper.*;
import org.xbery.overview.repo.AggType;
import org.xbery.overview.repo.Conditions;
import org.xbery.overview.repo.Repository;
import org.xbery.overview.repo.RepositoryException;
import org.xbery.overview.sql.filter.SqlCondition;
import org.xbery.overview.sql.filter.SqlConditionBuilder;
import org.xbery.overview.sql.mapper.JoinEntityMapper;
import org.xbery.overview.sql.mapper.ResultSetAttributeSource;
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
 * @param <T> type of entity
 * @param <K> type of entity key (composed keys are supported)
 * @param <F> type of filter
 * @author Radek Beran
 */
public abstract class AbstractSqlRepository<T, K, F> implements Repository<T, K, F> {

	protected static final Logger log = LoggerFactory.getLogger(AbstractSqlRepository.class);
	private static final SqlConditionBuilder sqlConditionBuilder = new SqlConditionBuilder();
	
	@Override
	public T create(T entity, boolean autogenerateKey) {
		Objects.requireNonNull(entity, "Entity should be specified");
		String tableName = getEntityMapper().getTableNameWithDb();
		String attributeNamesCommaSeparated = CollectionFuns.join(getEntityMapper().getAttributeNames(), ",");
		String questionMarks = getPlaceholdersCommaSeparated(getEntityMapper().getAttributeNames().size());
		List<Object> attributeValues = getEntityMapper().getAttributeValues(entity);

		String sql = "INSERT INTO " + tableName + " (" + attributeNamesCommaSeparated + ") VALUES (" + questionMarks + ")";

		T createdEntity = entity;
		K generatedKey = create(sql, attributeValues, autogenerateKey);

		if (autogenerateKey) {
			createdEntity = entityUpdatedWithId(createdEntity, generatedKey);
		}
		return createdEntity;
	}
	
	@Override
	public Optional<T> update(T entity) {
		Objects.requireNonNull(entity, "Entity should be specified");
		String attributeNamesEqToPlaceholders = getAttributeNamesEqToPlaceholdersCommaSeparated(getEntityMapper().getAttributeNames());
		int updatedCount = updateByFilterConditions(
			"UPDATE " + getEntityMapper().getTableNameWithDb() + " SET " + attributeNamesEqToPlaceholders,
			getEntityMapper().composeFilterConditionsForPrimaryKeyOfEntity(entity),
			getEntityMapper().getAttributeValues(entity));
		if (updatedCount == 1) {
			return Optional.<T>of(entity);
		}
		return Optional.<T>empty();
	}

	@Override
	public int update(K id, List<Pair<Attribute<T, ?>, Object>> attributesWithValues) {
		Objects.requireNonNull(id, "id should be specified");
		List<String> attributeNames = attributesWithValues.stream().map(p -> p.getFirst().getName()).collect(Collectors.toList());
		String attributeNamesEqToPlaceholders = getAttributeNamesEqToPlaceholdersCommaSeparated(attributeNames);
		List<Object> attributeValues = attributesWithValues.stream().map(p -> p.getSecond()).collect(Collectors.toList());
		return updateByFilterConditions(
			"UPDATE " + getEntityMapper().getTableNameWithDb() + " SET " + attributeNamesEqToPlaceholders,
			getEntityMapper().composeFilterConditionsForPrimaryKey(id),
			attributeValues);
	}

	@Override
	public boolean delete(K id) {
		Objects.requireNonNull(id, "id should be specified");
		return updateByFilterConditions("DELETE FROM " + getEntityMapper().getTableNameWithDb(),
			getEntityMapper().composeFilterConditionsForPrimaryKey(id), CollectionFuns.EMPTY_OBJECT_LIST) == 1;
	}

	@Override
	public int deleteByFilter(F filter) {
		Objects.requireNonNull(filter, "filter should be specified");
		return updateByFilterConditions("DELETE FROM " + getEntityMapper().getTableNameWithDb(),
			getEntityMapper().composeFilterConditions(filter), CollectionFuns.EMPTY_OBJECT_LIST);
	}

	/**
	 * Returns aggregated values of given attribute for given filter.
	 * @param aggType aggregation type
	 * @param resultClass
	 * @param attrName
	 * @param filter
	 * @param grouping
	 * @param entityMapper
	 * @param <R>
	 * @return
	 */
	@Override
	public <R, T, F> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter, List<Group> grouping, EntityMapper<T, F> entityMapper) {
		// TODO RBe: Count/aggreation with JoinWithManyEntityMapper

		Objects.requireNonNull(aggType, "aggregation type should be specified");
		Objects.requireNonNull(resultClass, "result class should be specified");
		Objects.requireNonNull(attrName, "attribute name should be specified");

		String aggAttributeAlias = attrName == "*" ? (aggType.name().toLowerCase() + "_agg") : (attrName + "_agg");
		List<R> results = queryWithOverview(
			aggFunction(aggType, attrName) + " AS " + aggAttributeAlias,
			entityMapper.getTableNameWithDb(),
			filter != null ? entityMapper.composeFilterConditions(filter) : new ArrayList<Condition>(),
			null,
			null,
			grouping,
			entityMapper,
			as -> as.get(resultClass, aggAttributeAlias));
		return results != null && !results.isEmpty() ? results.get(0) : null;
	}

    /**
     * Returns aggregated values of given attribute for given filter.
     * @param aggType aggregation type
     * @param resultClass
     * @param attrName
     * @param filter
	 * @param grouping
     * @param <R>
     * @return
     */
    @Override
    public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter, List<Group> grouping) {
        return aggByFilter(aggType, resultClass, attrName, filter, grouping, getEntityMapper());
    }

	@Override
	public <T, K, F>  Optional<T> findById(K id, EntityMapper<T, F> entityMapper) {
		return CollectionFuns.headOpt(findByFilterConditions(entityMapper.composeFilterConditionsForPrimaryKey(id), null, null, entityMapper));
	}

	@Override
	public <T, F> List<T> findByOverview(final Overview<F> overview, EntityMapper<T, F> entityMapper) {
		Objects.requireNonNull(overview, "overview should be specified");
		Objects.requireNonNull(entityMapper, "entityMapper should be specified");
		List<T> objects;
    	// TODO RBe: Perform JOIN for JoinWithManyEntityMapper on database level if pagination is not set
		if (isJoinWithManyMapper(entityMapper)) {
			objects = findJoinedWithMany(overview, (JoinEntityMapper)entityMapper);
		} else {
			List<String> attributeNames = entityMapper.getAttributeNames();
			String from = entityMapper.getTableNameWithDb();
			List<Condition> filterConditions = overview.getFilter() != null ? entityMapper.composeFilterConditions(overview.getFilter()) : new ArrayList<>();
			objects = queryWithOverview(attributeNames, from, filterConditions, overview.getOrdering(), overview.getPagination(), overview.getGrouping(), entityMapper, as -> entityMapper.buildEntity(as));
		}
		return objects;
	}

	@Override
	public List<T> findByOverview(final Overview<F> overview) {
		return findByOverview(overview, getEntityMapper());
	}

	protected abstract DataSource getDataSource();

    protected <T, F, U, G, V, H, O> List<V> findJoinedWithMany(final Overview<H> overview, JoinEntityMapper<T, F, U, G, V, H, O> joinedEntityMapper) {
		// Filters for first and second joined entity types
    	Pair<F, G> decomposedFilter = joinedEntityMapper.getDecomposeFilter().apply(overview.getFilter());
    	F firstEntityFilter = decomposedFilter.getFirst();
		G secondEntityFilter = decomposedFilter.getSecond();

		// Ordering for first and second joined entity types
		Pair<List<Order>, List<Order>> orders = joinedEntityMapper.getDecomposeOrdering().apply(overview.getOrdering());
		List<Order> firstEntityOrdering = orders.getFirst();
		List<Order> secondEntityOrdering = orders.getSecond();

		// Grouping for first and second joined entity types
		Pair<List<Group>, List<Group>> grouping = joinedEntityMapper.getDecomposeGrouping().apply(overview.getGrouping());
		List<Group> firstEntityGrouping = grouping.getFirst();
		List<Group> secondEntityGrouping = grouping.getSecond();

    	// First load all entities on the left side (entities of first type)
		Overview<F> firstEntityOverview = new Overview<>(firstEntityFilter, firstEntityOrdering, overview.getPagination(), firstEntityGrouping);
		List<T> firstEntities = findByOverview(firstEntityOverview, joinedEntityMapper.getFirstMapper());

		// Lazy loading of related right-side entities using one additional query (if they would be joined with left entities in one query, it could break pagination limit)
		EqAttributesCondition<T, U, O, O> eqAttrCondition = joinedEntityMapper.getJoinCondition();
		Attribute<T, O> firstEntityJoinAttr = eqAttrCondition.getFirstAttribute();
		Attribute<U, O> secondEntityJoinAttr = eqAttrCondition.getSecondAttribute();
		// Get all identifiers of first (=left) entities
		List<O> firstEntitiesIds = firstEntities.stream().map(e -> firstEntityJoinAttr.getValue(e)).collect(Collectors.toList());
		// Find second entities by these identifiers of first entities, with applying conditions from the second filter, ordering and grouping.
		// Pagination is not applied for the entities on the right (many) side.
		List<Condition> secondEntitiesConditions = new ArrayList<>();
		secondEntitiesConditions.add(Conditions.in(secondEntityJoinAttr, firstEntitiesIds));
		secondEntitiesConditions.addAll(joinedEntityMapper.getSecondMapper().composeFilterConditions(secondEntityFilter));
		List<U> secondEntities = findByFilterConditions(secondEntitiesConditions, secondEntityOrdering, secondEntityGrouping, joinedEntityMapper.getSecondMapper());

		// Attach second (many-side) entities to first entities
		List<V> firstEntitiesWithJoinedSecondEntities = new ArrayList<>();
		for (T firstEntity : firstEntities) {
			O firstEntityJoinValue = firstEntityJoinAttr.getValue(firstEntity);
			List<U> secondEntitiesForFirstEntity = secondEntities.stream().filter(secondEntity -> firstEntityJoinValue != null && firstEntityJoinValue.equals(secondEntityJoinAttr.getValue(secondEntity))).collect(Collectors.toList());
			V firstEntityWithJoinedSecondEntities = joinedEntityMapper.getComposeEntityWithMany().apply(firstEntity, secondEntitiesForFirstEntity);
			firstEntitiesWithJoinedSecondEntities.add(firstEntityWithJoinedSecondEntities);
		}
		return firstEntitiesWithJoinedSecondEntities;
	}

	protected int updateByFilterConditions(String cmdWithoutConditions, List<Condition> conditions, List<Object> updatedAttributeValues) {
		StringBuilder sqlBuilder = new StringBuilder(cmdWithoutConditions);

		final List<Object> parameterValues = new ArrayList<>();
		if (updatedAttributeValues != null) {
			parameterValues.addAll(getDbSupportedAttributeValues(updatedAttributeValues));
		}
		List<Object> pValues = appendFilter(sqlBuilder, conditions);
		if (pValues != null) {
			parameterValues.addAll(pValues);
		}
		return updateAttributeValues(sqlBuilder.toString(), parameterValues);
	}

	protected <T, F> List<T> findByOverview(Overview<F> overview, List<String> selectedAttributes, String from, EntityMapper<T, F> entityMapper) {
		return queryWithOverview(
			selectedAttributes,
			from,
			overview.getFilter() != null ? entityMapper.composeFilterConditions(overview.getFilter()) : null,
			overview.getOrdering(),
			overview.getPagination(),
			overview.getGrouping(),
			entityMapper,
			as -> entityMapper.buildEntity(as)
		);
	}

	protected <T, F> List<T> findByFilterConditions(List<Condition> filterConditions, List<Order> ordering, List<Group> grouping, EntityMapper<T, F> entityMapper) {
		return queryWithOverview(
			entityMapper.getAttributeNames(),
			entityMapper.getTableNameWithDb(),
			filterConditions,
			ordering,
			null,
			grouping,
			entityMapper,
			as -> entityMapper.buildEntity(as)
		);
	}

	/**
	 * Returns one entity for given unique attribute name and value.
	 * @param attribute
	 * @param attrValue
	 * @param <T>
 	 * @param <F>
	 * @param <U>
	 * @return
	 */
	protected <T, F, U> Optional<T> findByAttribute(Attribute<T, U> attribute, U attrValue, EntityMapper<T, F> entityMapper) {
		Objects.requireNonNull(attribute, "attribute should be specified");
		List<Condition> conditions = new ArrayList<>();
		conditions.add(Conditions.eq(attribute, attrValue));
		return CollectionFuns.headOpt(findByFilterConditions(conditions, null, null, entityMapper));
	}
	
	/**
	 * Returns entity with updated primary key attributes.
	 * This method can be overridden when you are working with immutable entity class.
	 * @param entity
	 * @param key
	 * @return entity updated with given id
	 */
	protected T entityUpdatedWithId(T entity, K key) {
		List<Pair<Attribute<T, ?>, Object>> attributesToValues = new ArrayList<>();
		attributesToValues.addAll(getEntityMapper().decomposePrimaryKey(key));

		// Fill in attribute source for binding key values to entity
		Map<String, Object> keyAttrSource = new LinkedHashMap<>();
		for (Pair<Attribute<T, ?>, Object> p : attributesToValues) {
			Attribute<T, ?> attr = p.getFirst();
			Object value = p.getSecond();
			keyAttrSource.put(attr.getName(), value);
		}
		MapAttributeSource attrSource = new MapAttributeSource(keyAttrSource);

		// Binding values of primary key parts to entity
		T updatedEntity = entity;
		for (Pair<Attribute<T, ?>, Object> p : attributesToValues) {
			Attribute<T, ?> attr = p.getFirst();
			updatedEntity = attr.entityWithAttribute(updatedEntity, attrSource, attr.getName());
		}
		return updatedEntity;
	}
	
	/**
	 * Subclasses should override this when the type of key is not compatible with class of first primary key attribute.
	 * @param generatedId
	 * @return
	 */
	protected K convertGeneratedKey(Object generatedId) {
		return (K)generatedId;
	}
	
	protected Date instantToUtilDate(Instant date) {
		if (date == null) return null;
		return new Date(date.toEpochMilli());
	}
	
	protected K create(String sql, List<Object> attributeValues, boolean autogenerateKey) {
		return withNewConnection(conn -> {
			K generatedKey = null;
			try (PreparedStatement statement = conn.prepareStatement(sql, autogenerateKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)) {
				setParameters(statement, getDbSupportedAttributeValues(attributeValues));
				if (autogenerateKey) {
					statement.executeUpdate();
					ResultSet rs = statement.getGeneratedKeys();
					rs.next();
					generatedKey = getAutogeneratedKey(rs);
				} else {
					statement.executeUpdate();
				}
				logSqlWithParameters(statement.toString(), attributeValues);
				return generatedKey;
			} catch (Exception ex) {
				throw new RepositoryException(ex.getMessage(), ex);
			}
		});
	}

	protected K getAutogeneratedKey(ResultSet rs) {
		try {
			// Extract corresponding type of first primary key attribute from result set
			List<Attribute<T, ?>> pkAttributes = getEntityMapper().getPrimaryAttributes();
			Class<?> firstPKClass = !pkAttributes.isEmpty() ? pkAttributes.get(0).getAttributeClass() : null;
			Object pkValue = null;
			if (firstPKClass != null && Integer.class.isAssignableFrom(firstPKClass)) {
				// TODO: Use ResultSetAttributeSource for this?
				pkValue = rs.getInt(1);
			} else {
				pkValue = rs.getLong(1);
			}

			return convertGeneratedKey(pkValue);
		} catch (Exception ex) {
			throw new RepositoryException(ex.getMessage(), ex);
		}
	}

	protected int updateAttributeValues(String sql, List<Object> attributeValues) {
		return withNewConnection(conn -> {
			try (PreparedStatement statement = conn.prepareStatement(sql)) {
				setParameters(statement, attributeValues);
				int updatedCount = statement.executeUpdate();
				logSqlWithParameters(statement.toString(), attributeValues);
				return updatedCount;
			} catch (Exception ex) {
				throw new RepositoryException(ex.getMessage(), ex);
			}
		});
	}

	// Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
	protected <T, F> List<T> queryWithOverview(
		List<String> selectedAttributes,
		String from,
		List<Condition> filterConditions,
		List<Order> ordering,
		Pagination pagination,
		List<Group> grouping,
		EntityMapper<T, F> entityMapper,
		Function<AttributeSource, T> entityBuilder) {

		return queryWithOverview(
			CollectionFuns.join(selectedAttributes, ", "),
			from,
			filterConditions,
			ordering,
			pagination,
			grouping,
			entityMapper,
			entityBuilder
		);
	}

	// Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
	protected <T, F, R> List<R> queryWithOverview(
		String selection,
		String from,
		List<Condition> filterConditions,
		List<Order> ordering,
		Pagination pagination,
		List<Group> grouping,
		EntityMapper<T, F> entityMappper,
		Function<AttributeSource, R> entityBuilder) {

		return withNewConnection(conn -> {
			List<R> results = new ArrayList<>();
			try {
				StringBuilder sqlBuilder = new StringBuilder("SELECT " + selection + " FROM " + from);
				List<Object> parameters = appendFilter(sqlBuilder, filterConditions);
				appendGrouping(sqlBuilder, (grouping == null || grouping.isEmpty()) ? entityMappper.defaultGrouping() : grouping);
				appendOrdering(sqlBuilder, (ordering == null || ordering.isEmpty()) ? entityMappper.defaultOrdering() : ordering);
				appendPagination(sqlBuilder, pagination);

				String sql = sqlBuilder.toString();

				try (PreparedStatement statement = conn.prepareStatement(sql)) {
					setParameters(statement, parameters);

					try (ResultSet rs = statement.executeQuery()) {
						while (rs.next()) {
							results.add(entityBuilder.apply(new ResultSetAttributeSource(rs)));
						}
					}
				}

				logSqlWithParameters(sql, parameters);
			} catch (Exception ex) {
				throw new RepositoryException(ex.getMessage(), ex);
			}
			return results;
		});
	}

	/**
	 * Returns results along with overview (filtering, sorting, grouping and pagination) settings. Pagination settings is returned filled with total
	 * count of records - this count is loaded using separate count query.
	 * @param projection
	 * @param overview
	 * @param entityMapper
	 * @param <T>
	 * @param <F>
	 * @return
	 */
	protected <T, F> ResultsWithOverview<T, F> findResultsWithOverviewAndProjection(String projection, Overview<F> overview, EntityMapper<T, F> entityMapper) {
		String from = entityMapper.getTableNameWithDb();
		List<Condition> filterConditions = overview.getFilter() != null ? entityMapper.composeFilterConditions(overview.getFilter()) : new ArrayList<>();
		List<T> entities = queryWithOverview(projection, from, filterConditions, overview.getOrdering(), overview.getPagination(), overview.getGrouping(), entityMapper, as -> entityMapper.buildEntity(as));

		String aggAlias = "attr_count";
		List<Integer> countRes = queryWithOverview("COUNT(" + projection + ") AS " + aggAlias, from, filterConditions, null, null, overview.getGrouping(), entityMapper, as -> as.get(Integer.class, aggAlias));
		Integer totalCount = (countRes != null && !countRes.isEmpty()) ? countRes.get(0) : null;

		return new ResultsWithOverview<>(entities, overview.withPagination(overview.getPagination().withTotalCount(totalCount)));
	}

	protected List<Object> appendFilter(StringBuilder sqlBuilder, List<Condition> filterConditions) {
		List<Object> parameters = null;
		if (filterConditions != null && !filterConditions.isEmpty()) {
			List<SqlCondition> sqlConditions = filterConditions.stream().map(c -> getConditionBuilder().build(c, this::getDbSupportedAttributeValue)).collect(Collectors.toList());
			List<String> whereClause = sqlConditions.stream().map(c -> c.getConditionWithPlaceholders()).collect(Collectors.toList());
			parameters = sqlConditions.stream().flatMap(c -> c.getValues().stream()).collect(Collectors.toList());
			sqlBuilder.append(" WHERE ").append(CollectionFuns.join(whereClause, " AND "));
		}
		return parameters;
	}

	protected void appendGrouping(StringBuilder sqlBuilder, List<Group> grouping) {
		if (grouping != null && !grouping.isEmpty()) {
			List<String> groupByAttributes = grouping.stream().map(c -> c.getAttribute()).collect(Collectors.toList());
			sqlBuilder.append(" GROUP BY ").append(CollectionFuns.join(groupByAttributes, ", "));
		}
	}

	protected void appendOrdering(StringBuilder sqlBuilder, List<Order> ordering) {
		if (ordering != null && !ordering.isEmpty()) {
			List<String> orderByAttributes = ordering.stream().map(c -> c.getDbString()).collect(Collectors.toList());
			sqlBuilder.append(" ORDER BY ").append(CollectionFuns.join(orderByAttributes, ", "));
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

	protected List<Object> getDbSupportedAttributeValues(List<Object> attributeValues) {
		List<Object> result = new ArrayList<>();
		if (attributeValues != null) {
			for (Object v : attributeValues) {
				result.add(getDbSupportedAttributeValue(v));
			}
		}
		return result;
	}

	protected Object getDbSupportedAttributeValue(Object v) {
		Object valueForDb = null;
		// TODO RBe: Conversion of another data types when not supported their passing to JDBC?
		if (v instanceof Instant) {
			valueForDb = instantToUtilDate((Instant)v);
		} else {
			valueForDb = v;
		}
		return valueForDb;
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

	protected <U> U withNewConnection(CheckedFunction<Connection, U> queryData) {
		Connection conn = null;
		boolean success = false;
		U result = null;
		try {
			conn = getDataSource().getConnection();
			result = queryData.apply(conn);
			success = true;
		} catch (Exception ex) {
			throw new RepositoryException(ex.getMessage(), ex);
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
					throw new RepositoryException(ex.getMessage(), ex);
				} finally {
					try {
						conn.close();
					} catch (SQLException e) {
						throw new RepositoryException(e.getMessage(), e);
					}
				}
			}
		}
		return result;
	}

	/** Returns string with comma-separated question marks, one for each database column name. */
	protected String getPlaceholdersCommaSeparated(int count) {
		return CollectionFuns.join(Collections.nCopies(count, "?"), ",");
	}

	/** Returns string with comma-separated database attribute names with placeholder values in form suitable for SQL update: column1=?,column2=?,... */
	protected String getAttributeNamesEqToPlaceholdersCommaSeparated(List<String> attributeNames) {
		return CollectionFuns.join(attributeNames.stream().map(attrName -> attrName + "=?").collect(Collectors.toList()), ",");
	}

	protected SqlConditionBuilder getConditionBuilder() {
		return sqlConditionBuilder;
	}

	private <T, F> boolean isJoinWithManyMapper(EntityMapper<T, F> entityMappper) {
		if (!(entityMappper instanceof JoinEntityMapper)) {
			return false;
		}
		JoinEntityMapper joinMapper = (JoinEntityMapper)entityMappper;
		return joinMapper.getCardinality() != null && joinMapper.getCardinality() == Cardinality.MANY;
	}
}
