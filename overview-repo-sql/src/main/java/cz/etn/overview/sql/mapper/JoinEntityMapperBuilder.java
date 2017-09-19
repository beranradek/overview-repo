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
 * @author Radek Beran
 */
public class JoinEntityMapperBuilder<T, F, U, G, V, H, O> {

    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;
    private final JoinType joinType;

    private Cardinality cardinality;
    private EqAttributesCondition<T, U, O, O> joinCondition;
    private List<Condition> additionalOnConditions;
    private BiFunction<T, U, V> composeEntity;
    private BiFunction<T, List<U>, V> composeEntityWithMany;
    private Function<H, Pair<F, G>> decomposeFilter;
    private Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder = Joins.DEFAULT_ORDERING_DECOMPOSITION;

    public JoinEntityMapperBuilder(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, JoinType joinType) {
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.joinType = joinType;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute, List<Condition> additionalOnConditions) {
        this.joinCondition = Conditions.eqAttributes(firstAttribute, secondAttribute);
        this.additionalOnConditions = additionalOnConditions;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> on(Attribute<T, O> firstAttribute, Attribute<U, O> secondAttribute) {
        return on(firstAttribute, secondAttribute, CollectionFuns.empty());
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> composeEntity(BiFunction<T, U, V> composeEntity) {
        this.composeEntity = composeEntity;
        this.cardinality = Cardinality.ONE;
        return this;
    }

    /**
     * Composition of first entity with many records of second entity.
     * @param composeEntityWithMany
     * @return
     */
    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> composeEntityWithMany(BiFunction<T, List<U>, V> composeEntityWithMany) {
        this.composeEntityWithMany = composeEntityWithMany;
        this.cardinality = Cardinality.MANY;
        return this;
    }

    /**
     * Filter decomposition to first and second entity filters.
     * @param decomposeFilter
     * @return
     */
    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> decomposeFilter(Function<H, Pair<F, G>> decomposeFilter) {
        this.decomposeFilter = decomposeFilter;
        return this;
    }

    public JoinEntityMapperBuilder<T, F, U, G, V, H, O> decomposeOrder(Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder) {
        this.decomposeOrder = decomposeOrder;
        return this;
    }

    public JoinEntityMapper<T, F, U, G, V, H, O> build() {
        // TODO: Checks on required fields
        return new JoinEntityMapper<T, F, U, G, V, H, O>(firstMapper, secondMapper, joinType, cardinality, joinCondition, additionalOnConditions, composeEntity, composeEntityWithMany, decomposeFilter, decomposeOrder);
    }

}
