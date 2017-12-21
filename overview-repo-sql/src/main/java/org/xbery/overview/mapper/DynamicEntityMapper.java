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
package org.xbery.overview.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<T, F> implements EntityMapper<T, F> {

    private final Map<String, Attribute<T, ?>> attributesByFullNames;

    public DynamicEntityMapper() {
        attributesByFullNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attributeBuilder filled builder of new attribute (attribute must not be already registered)
     */
    public synchronized <A> Attribute<T, A> add(Attr.Builder<T, A> attributeBuilder) {
        return add(attributeBuilder.build());
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attribute new attribute (attribute must not be already registered)
     */
    public synchronized <A> Attribute<T, A> add(Attribute<T, A> attribute) {
        if (attribute.getNamePrefix() == null) {
            // name prefix not set yet
            attribute = attribute.withNamePrefix(getNamePrefix());
        }

        return addUnchanged(attribute);
    }

    @Override
    public List<Attribute<T, ?>> getAttributes() {
        return new ArrayList<>(attributesByFullNames.values());
    }

    protected synchronized <A> Attribute<T, A> addUnchanged(Attribute<T, A> attribute) {
        if (attributesByFullNames.containsKey(attribute.getNameFull())) {
            throw new IllegalStateException("Attribute " + attribute.getNameFull() + " is already registered");
        }
        attributesByFullNames.put(attribute.getNameFull(), attribute);
        return attribute;
    }

    protected String getNamePrefix() {
        return getTableName();
    }
}
