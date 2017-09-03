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
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.mapper.JoinType;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Entity mapper that is result of join operation between the first and second joined mapper.
 * Second mapper is used to fetch many entities that are bound to the corresponding one entity fetched by first mapper.
 * It implements an {@link EntityMapper} capable to fetch joined entities (in 1:n cardinality).
 * @author Radek Beran
 * @param <T> type of first joined entity
 * @param <F> type of filter for first entity
 * @param <U> type of second joined entity
 * @param <G> type of filter for second entity
 * @param <V> type of resulting entity
 * @param <H> type of filter for resulting entity
 * @param <O> type of attribute of first and second entity in join condition
 */
public class JoinWithManyEntityMapper<T, F, U, G, V, H, O> extends JoinEntityMapper<T, F, U, G, V, H> {

    private final EqAttributesCondition<T, U, O, O> joinWithManyCondition;
    private final BiFunction<T, List<U>, V> composeEntityWithMany;
    private final Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder;

    public JoinWithManyEntityMapper(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, EqAttributesCondition<T, U, O, O> joinWithManyCondition, List<Condition> additionalOnConditions, BiFunction<T, List<U>, V> composeEntityWithMany, Function<H, Pair<F, G>> decomposeFilter, Function<List<Order>, Pair<List<Order>, List<Order>>> decomposeOrder) {
        super(firstMapper, secondMapper,
            CollectionFuns.listWithPrepended(additionalOnConditions, joinWithManyCondition),
            createComposeEntityFun(composeEntityWithMany),
            decomposeFilter,
            JoinType.LEFT);
        this.joinWithManyCondition = joinWithManyCondition;
        this.composeEntityWithMany = composeEntityWithMany;
        this.decomposeOrder = decomposeOrder;
    }

    protected static <T, U, V> BiFunction<T, U, V> createComposeEntityFun(BiFunction<T, List<U>, V> composeEntityWithMany) {
        return (e1, e2) -> composeEntityWithMany.apply(e1, CollectionFuns.singleValueList(e2));
    }

    public BiFunction<T, List<U>, V> getComposeEntityWithMany() {
        return composeEntityWithMany;
    }

    public EqAttributesCondition<T, U, O, O> getJoinWithManyCondition() {
        return joinWithManyCondition;
    }

    public Function<List<Order>, Pair<List<Order>, List<Order>>> getDecomposeOrder() {
        return decomposeOrder;
    }
}
