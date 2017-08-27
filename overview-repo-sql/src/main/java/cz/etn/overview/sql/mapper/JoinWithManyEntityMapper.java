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

import cz.etn.overview.common.Pair;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Entity mapper that is result of join operation between the first and second joined mapper.
 * Second mapper is used to fetch many entities that are bound to the corresponding one entity fetched by first mapper.
 * It implements an {@link EntityMapper} capable to fetch joined entities (in 1:n cardinality).
 * @author Radek Beran
 */
// TODO RBe: Should this class still implement EntityMapper interface?
public class JoinWithManyEntityMapper<T, F, U, G, V, H> implements EntityMapper<V, H> {

    private final EntityMapper<T, F> firstMapper;
    private final EntityMapper<U, G> secondMapper;
    private final List<Condition> onConditions;
    private final BiFunction<T, List<U>, V> composeEntities;
    // TODO RBe: remove decomposeFilter if not used
    private final Function<H, Pair<F, G>> decomposeFilter;

    public JoinWithManyEntityMapper(EntityMapper<T, F> firstMapper, EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, List<U>, V> composeEntities, Function<H, Pair<F, G>> decomposeFilter) {
        this.firstMapper = firstMapper;
        this.secondMapper = secondMapper;
        this.onConditions = onConditions;
        this.composeEntities = composeEntities;
        this.decomposeFilter = decomposeFilter;
    }

    public EntityMapper<T, F> getFirstMapper() {
        return firstMapper;
    }

    public EntityMapper<U, G> getSecondMapper() {
        return secondMapper;
    }

    @Override
    public V createEntity() {
        throw new UnsupportedOperationException("Create entity not supported by " + getClass().getSimpleName());
    }

    @Override
    public V buildEntity(AttributeSource attributeSource, String ignored) {
        throw new UnsupportedOperationException("Build entity not supported by " + getClass().getSimpleName());
    }

    @Override
    public String getDataSet() {
        throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName());
    }

    @Override
    public List<Condition> composeFilterConditions(H filter) {
        throw new UnsupportedOperationException("Compose filter conditions not supported by " + getClass().getSimpleName());
    }

    @Override
    public List<String> getAttributeNames() {
        throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName());
    }

    @Override
    public List<String> getAttributeNamesFullAliased() {
        throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName());
    }

    @Override
    public String getAliasPrefix() {
        throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName());
    }

    @Override
    public List<Attribute<V, ?>> getAttributes() {
        throw new UnsupportedOperationException("Not supported by " + getClass().getSimpleName());
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
}
