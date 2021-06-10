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
package org.xbery.overview.sql.mapper;

import org.xbery.overview.Group;
import org.xbery.overview.Order;
import org.xbery.overview.common.Pair;
import org.xbery.overview.common.funs.CollectionFuns;
import org.xbery.overview.filter.Condition;
import org.xbery.overview.filter.EqAttributesCondition;
import org.xbery.overview.mapper.*;
import org.xbery.overview.sql.filter.SqlCondition;
import org.xbery.overview.sql.filter.SqlConditionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Entity mapper that is result of join operation between the first and second joined mapper.
 * It implements an {@link EntityMapper} capable to fetch joined entities.
 * @author Radek Beran
 * @param <T> type of first joined entity
 * @param <F> type of filter for first entity
 * @param <U> type of second joined entity
 * @param <G> type of filter for second entity
 * @param <V> type of resulting entity
 * @param <H> type of filter for resulting entity
 * @param <O> type of attribute used for join operation
 */
public class JoinEntityMapper<T, F, U, G, V, H, O> implements EntityMapper<V, H> {

    private static final SqlConditionBuilder sqlConditionBuilder = new SqlConditionBuilder();
    private static final DbTypeConvertor dbTypeConvertor = new DbTypeConvertor();
    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;

    private final EqAttributesCondition<T, U, O, O> joinCondition;
    private final List<Condition> additionalOnConditions;
    private final BiFunction<T, U, V> composeEntity;
    private final BiFunction<T, List<U>, V> composeEntityWithMany;
    private final Function<H, Pair<F, G>> decomposeFilter;
    private final JoinType joinType;
    private final Cardinality cardinality;
    private final Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrdering;
    private final Function<List<Group>, Pair<List<Group>, List<Group>>> decomposeGrouping;

    JoinEntityMapper(
        EntityMapper<T, F> firstMapper,
        EntityMapper<U, G> secondMapper,
        JoinType joinType,
        Cardinality cardinality,
        EqAttributesCondition<T, U, O, O> joinCondition,
        List<Condition> additionalOnConditions,
        BiFunction<T, U, V> composeEntity,
        BiFunction<T, List<U>, V> composeEntityWithMany,
        Function<H, Pair<F, G>> decomposeFilter,
        Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrdering,
        Function<List<Group>, Pair<List<Group>, List<Group>>> decomposeGrouping
        ) {

        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.joinCondition = joinCondition;
        this.additionalOnConditions = additionalOnConditions;
        this.composeEntity = composeEntity;
        this.composeEntityWithMany = composeEntityWithMany;
        this.decomposeFilter = decomposeFilter;
        this.joinType = joinType;
        this.cardinality = cardinality;
        this.decomposeOrdering = decomposeOrdering;
        this.decomposeGrouping = decomposeGrouping;
    }

    public EntityMapper<T, F> getFirstMapper() {
        return firstMapper;
    }

    public EntityMapper<U, G> getSecondMapper() {
        return secondMapper;
    }

    public List<Condition> getOnConditions() {
        return CollectionFuns.listWithPrepended(additionalOnConditions, joinCondition);
    }

    public EqAttributesCondition<T, U, O, O> getJoinCondition() {
        return joinCondition;
    }

    @Override
    public V createEntity(AttributeSource attributeSource, List<Attribute<V, ?>> attributes, String aliasPrefix) {
        Set<String> firstAttrNames = firstMapper.getAttributeNames().stream().collect(Collectors.toSet());
        Set<String> secondAttrNames = secondMapper.getAttributeNames().stream().collect(Collectors.toSet());
        List<Attribute<T, ?>> firstAttributes = convertInstanceOfObject(attributes.stream().filter(a -> firstAttrNames.contains(a.getName())).collect(Collectors.toList()), List.class);
        List<Attribute<U, ?>> secondAttributes = convertInstanceOfObject(attributes.stream().filter(a -> secondAttrNames.contains(a.getName())).collect(Collectors.toList()), List.class);
        return composeEntity.apply(firstMapper.createEntity(attributeSource, firstAttributes, aliasPrefix), secondMapper.createEntity(attributeSource, secondAttributes, aliasPrefix));
    }

    @Override
    public V buildEntity(AttributeSource attributeSource, String ignored) {
        T firstEntity = firstMapper.buildEntity(attributeSource, firstMapper.getAliasPrefix());
        U secondEntity = secondMapper.buildEntity(attributeSource, secondMapper.getAliasPrefix());
        return composeEntity.apply(firstEntity, secondEntity);
    }

    @Override
    public String getTableName() {
        StringBuilder sqlBuilder = new StringBuilder(firstMapper.getTableNameWithDb() + " " + joinType.name() + " JOIN " + secondMapper.getTableName());
        // TODO RBe: Do not duplicate this condition transformation logic with repository
        List<Condition> onConditions = getOnConditions();
        if (onConditions != null && !onConditions.isEmpty()) {
            List<SqlCondition> sqlConditions = onConditions.stream().map(c -> getConditionBuilder().build(c, getDbTypeConvertor()::toDbValue)).collect(Collectors.toList());
            List<String> onClause = sqlConditions.stream().map(c -> c.getConditionWithPlaceholders()).collect(Collectors.toList());
            List<Object> parameters = sqlConditions.stream().flatMap(c -> c.getValues().stream()).collect(Collectors.toList());
            if (parameters != null && !parameters.isEmpty()) {
                throw new IllegalArgumentException("Placeholders in JOIN ON CLAUSE are not supported, please use attributes only or concrete values that do not come from user input");
            }
            sqlBuilder.append(" ON (").append(CollectionFuns.join(onClause, " AND ")).append(")");
        }
        return sqlBuilder.toString();
    }

    @Override
    public List<Condition> composeFilterConditions(H filter) {
        List<Condition> conditions = new ArrayList<>();
        Pair<F, G> filters = decomposeFilter.apply(filter);
        if (filters.getFirst() != null) {
            conditions.addAll(firstMapper.composeFilterConditions(filters.getFirst()));
        }
        if (filters.getSecond() != null) {
            conditions.addAll(secondMapper.composeFilterConditions(filters.getSecond()));
        }
        return conditions;
    }

    @Override
    public List<String> getAttributeNames() {
        // Full names with aliases will be automatically used in queries selection
        return getAttributeNamesFullAliased();
    }

    @Override
    public List<String> getAttributeNamesFullAliased() {
        List<String> names = new ArrayList<>();
        names.addAll(firstMapper.getAttributeNamesFullAliased());
        names.addAll(secondMapper.getAttributeNamesFullAliased());
        return names;
    }

    @Override
    public String getAliasPrefix() {
        return firstMapper.getTableName() + "_" + secondMapper.getTableName() + "_";
    }

    @Override
    public List<Attribute<V, ?>> getAttributes() {
        // TODO RBe: Throw throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName()); ?
        return new ArrayList<>(); // join mapper has not its own attributes, it composes attributes of first and second joined mapper
    }

    @Override
    public List<String> getPrimaryAttributeNames() {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }

    @Override
    public List<Object> getPrimaryAttributeValues(V entity) {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }

    @Override
    public List<Object> getAttributeValues(V instance) {
        throw new UnsupportedOperationException("Unsupported operation in joined mapper");
    }

    public Function<List<Order>, Pair<List<Order>, List<Order>>> getDecomposeOrdering() {
        return decomposeOrdering;
    }

    public Function<List<Group>, Pair<List<Group>, List<Group>>> getDecomposeGrouping() {
        return decomposeGrouping;
    }

    public BiFunction<T, U, V> getComposeEntity() {
        return composeEntity;
    }

    public BiFunction<T, List<U>, V> getComposeEntityWithMany() {
        return composeEntityWithMany;
    }

    public Function<H, Pair<F, G>> getDecomposeFilter() {
        return decomposeFilter;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    protected SqlConditionBuilder getConditionBuilder() {
        return sqlConditionBuilder;
    }

    protected DbTypeConvertor getDbTypeConvertor() {
        return dbTypeConvertor;
    }

    private <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch (ClassCastException e) {
            return null;
        }
    }
}
