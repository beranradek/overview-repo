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
package cz.etn.overview.sql.mapper;

import cz.etn.overview.Group;
import cz.etn.overview.Order;
import cz.etn.overview.common.Pair;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.filter.EqAttributesCondition;
import cz.etn.overview.mapper.*;
import cz.etn.overview.repo.Conditions;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Builder for {@link JoinEntityMapper}.
 * @param <T> type of first joined entity
 * @param <F> type of filter for first entity
 * @param <U> type of second joined entity
 * @param <G> type of filter for second entity
 * @author Radek Beran
 */
public class JoinEntityMapperBuilder<T, F, U, G> {

    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;
    private final JoinType joinType;

    private Cardinality cardinality;
    private EqAttributesCondition<T, U, ?, ?> joinCondition;
    private List<Condition> additionalOnConditions;
    private BiFunction<T, U, ?> composeEntity;
    private BiFunction<T, List<U>, ?> composeEntityWithMany;
    private Function<?, Pair<F, G>> decomposeFilter;
    private Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrdering = Decompose.DEFAULT_ORDERING_DECOMPOSITION;
    private Function<List<Group>, Pair<List<Group>, List<Group>>> decomposeGrouping = Decompose.DEFAULT_GROUPING_DECOMPOSITION;

    public JoinEntityMapperBuilder(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, JoinType joinType) {
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.joinType = joinType;
    }

    public <O> JoinEntityMapperBuilder<T, F, U, G> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute, List<Condition> additionalOnConditions) {
        this.joinCondition = Conditions.eqAttributes(firstAttribute, secondAttribute);
        this.additionalOnConditions = additionalOnConditions;
        return this;
    }

    public <O> JoinEntityMapperBuilder<T, F, U, G> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute) {
        return on(firstAttribute, secondAttribute, CollectionFuns.emptyList());
    }

    public <V> JoinEntityMapperBuilder<T, F, U, G> composeEntity(BiFunction<T, U, V> composeEntity) {
        this.composeEntity = composeEntity;
        this.cardinality = Cardinality.ONE;
        return this;
    }

    /**
     * Composition of first entity with many records of second entity.
     * @param composeEntityWithMany
     * @return
     */
    public <V> JoinEntityMapperBuilder<T, F, U, G> composeEntityWithMany(BiFunction<T, List<U>, V> composeEntityWithMany) {
        this.composeEntityWithMany = composeEntityWithMany;
        this.cardinality = Cardinality.MANY;
        return this;
    }

    /**
     * Filter decomposition to first and second entity filters.
     * @param decomposeFilter
     * @return
     */
    public <H> JoinEntityMapperBuilder<T, F, U, G> decomposeFilter(Function<H, Pair<F, G>> decomposeFilter) {
        this.decomposeFilter = decomposeFilter;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G> decomposeOrdering(Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrdering) {
        this.decomposeOrdering = decomposeOrdering;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G> decomposeGrouping(Function<List<Group>, Pair<List<Group>, List<Group>>> decomposeGrouping) {
        this.decomposeGrouping = decomposeGrouping;
        return this;
    }

    public EntityMapper<T, F> getFirstMapper() {
        return firstMapper;
    }

    public EntityMapper<U, G> getSecondMapper() {
        return secondMapper;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public EqAttributesCondition<T, U, ?, ?> getJoinCondition() {
        return joinCondition;
    }

    public List<Condition> getAdditionalOnConditions() {
        return additionalOnConditions;
    }

    public BiFunction<T, U, ?> getComposeEntity() {
        return composeEntity;
    }

    public BiFunction<T, List<U>, ?> getComposeEntityWithMany() {
        return composeEntityWithMany;
    }

    public Function<?, Pair<F, G>> getDecomposeFilter() {
        return decomposeFilter;
    }

    public Function<List<Order>, Pair<List<Order>, List<Order>>> getDecomposeOrdering() {
        return decomposeOrdering;
    }

    public Function<List<Group>, Pair<List<Group>, List<Group>>> getDecomposeGrouping() {
        return decomposeGrouping;
    }

    public <V, H, O> JoinEntityMapper<T, F, U, G, V, H, O> build() {
        // TODO: Checks on required fields
        return new JoinEntityMapper<>(firstMapper, secondMapper, joinType, cardinality,
            (EqAttributesCondition<T, U, O, O>)joinCondition,
            additionalOnConditions,
            (BiFunction<T, U, V>)composeEntity,
            (BiFunction<T, List<U>, V>)composeEntityWithMany,
            (Function<H, Pair<F, G>>)decomposeFilter,
            decomposeOrdering,
            decomposeGrouping);
    }

}
