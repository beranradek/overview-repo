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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * Attribute class with its builder for convenient construction of instance
 * with many parameters. This attribute implementation supports name prefixes.
 * <p>
 * Example of construction:
 * {@code Attribute attr = new Attribute.of(..).mapping(...)...build();}
 * <p>
 * Thread-safe: Yes, instance is immutable.
 *
 * @author Radek Beran
 */
public class Attr<E, A> implements Attribute<E, A> {
    private final Class<E> entityClass;
    private final Class<A> attributeClass;
    private final String name;
    private final boolean primary;
    private final Function<E, A> fromEntity;
    private final String namePrefix;
    private final Optional<Integer> maxLength;

    public static <E, A> Builder<E, A> of(Class<E> entityClass, Class<A> attributeClass, String name) {
        return new Builder<>(entityClass, attributeClass, name);
    }

    public static <E> Builder<E, Date> ofDate(Class<E> entityClass, String name) {
        return of(entityClass, Date.class, name);
    }

    public static <E> Builder<E, Instant> ofInstant(Class<E> entityClass, String name) {
        return of(entityClass, Instant.class, name);
    }

    public static <E> Builder<E, Byte> ofByte(Class<E> entityClass, String name) {
        return of(entityClass, Byte.class, name);
    }

    public static <E> Builder<E, Integer> ofInteger(Class<E> entityClass, String name) {
        return of(entityClass, Integer.class, name);
    }

    public static <E> Builder<E, Long> ofLong(Class<E> entityClass, String name) {
        return of(entityClass, Long.class, name);
    }

    public static <E> Builder<E, Float> ofFloat(Class<E> entityClass, String name) {
        return of(entityClass, Float.class, name);
    }

    public static <E> Builder<E, Double> ofDouble(Class<E> entityClass, String name) {
        return of(entityClass, Double.class, name);
    }

    public static <E> Builder<E, Boolean> ofBoolean(Class<E> entityClass, String name) {
        return of(entityClass, Boolean.class, name);
    }

    public static <E> Builder<E, String> ofString(Class<E> entityClass, String name) {
        return of(entityClass, String.class, name);
    }

    public static <E> Builder<E, BigDecimal> ofBigDecimal(Class<E> entityClass, String name) {
        return of(entityClass, BigDecimal.class, name);
    }

    public static <T extends Enum<T>> T getEnumValueFromSource(Class<T> enumType, Attribute<?, String> attr, AttributeSource attributeSource, String aliasPrefix) {
        String str = attr.getValueFromSource(attributeSource, aliasPrefix);
        T enumValue = null;
        if (str != null && !str.isEmpty()) {
            enumValue = Enum.valueOf(enumType, str);
        }
        return enumValue;
    }

    /**
     * Copy constructor.
     */
    public Attr(Attr source, String namePrefix) {
        this.entityClass = source.entityClass;
        this.attributeClass = source.attributeClass;
        this.name = source.name;
        this.primary = source.primary;
        this.fromEntity = source.fromEntity;
        this.namePrefix = namePrefix;
        this.maxLength = source.maxLength;
    }

    public static class Builder<E, A> {
        // Required parameters
        private final Class<E> entityClass;
        private final Class<A> attributeClass;
        private final String name;
        private Function<E, A> fromEntity;
        private String namePrefix;

        // Optional parameters - initialized to default values (these are only here in a single location)
        private boolean primary = false;
        private Optional<Integer> maxLength = Optional.empty();

        public Builder(Class<E> entityClass, Class<A> attributeClass, String name) {
            this.entityClass = entityClass;
            this.attributeClass = attributeClass;
            this.name = name;
        }

        public Builder<E, A> primary() {
            return primary(true);
        }

        public Builder<E, A> primary(boolean primaryAttribute) {
            primary = primaryAttribute;
            return this;
        }

        public Builder<E, A> maxLength(Integer length) {
            maxLength = Optional.ofNullable(length);
            return this;
        }

        public Builder<E, A> maxLength(Optional<Integer> lengthOpt) {
            maxLength = lengthOpt;
            return this;
        }

        public Builder<E, A> get(Function<E, A> fromEntity) {
            this.fromEntity = fromEntity;
            return this;
        }

        public Builder<E, A> namePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
            return this;
        }

        public Class<E> getEntityClass() {
            return entityClass;
        }

        public Class<A> getAttributeClass() {
            return attributeClass;
        }

        public String getName() {
            return name;
        }

        public Function<E, A> getFromEntity() {
            return fromEntity;
        }

        public String getNamePrefix() {
            return namePrefix;
        }

        public boolean isPrimary() {
            return primary;
        }

        public Optional<Integer> getMaxLength() {
            return maxLength;
        }

        public Attr build() {
            Attr attr = new Attr(this);
            // Possible validations here (checks on fields)...
            if (fromEntity == null) {
                throw new IllegalStateException("get mapping is missing");
            }
            return attr;
        }
    }

    private Attr(Builder<E, A> builder) {
        entityClass = builder.entityClass;
        attributeClass = builder.attributeClass;
        name = builder.name;
        primary = builder.primary;
        fromEntity = builder.fromEntity;
        namePrefix = builder.namePrefix;
        maxLength = builder.maxLength;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<A> getAttributeClass() {
        return attributeClass;
    }

    @Override
    public A getValue(E entity) {
        return fromEntity.apply(entity);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isPrimary() {
        return primary;
    }

    @Override
    public Optional<Integer> getMaxLength() {
        return maxLength;
    }

    public Function<E, A> getFromEntity() {
        return fromEntity;
    }

    @Override
    public String getNamePrefix() {
        return namePrefix;
    }

    @Override
    public Attribute<E, A> withNamePrefix(String namePrefix) {
        return new Attr(this, namePrefix);
    }

    @Override
    public <T> Attribute<E, T> as(Class<T> attrClass, Function<A, T> toNewType, Function<T, A> toOldType) {
        return Attr.of(entityClass, attrClass, name)
            .get(e -> { A v = fromEntity.apply(e); return v != null ? toNewType.apply(v) : null; })
            .primary(primary)
            .namePrefix(namePrefix)
            .maxLength(maxLength)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attr)) return false;
        Attr<?, ?> attr = (Attr<?, ?>) o;
        if (!entityClass.equals(attr.entityClass)) return false;
        return name.equals(attr.name);
    }

    @Override
    public int hashCode() {
        int result = entityClass.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
