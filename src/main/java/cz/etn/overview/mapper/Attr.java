package cz.etn.overview.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Attribute class with its builder for convenient construction of instance
 * with many parameters.
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
    private final BiFunction<E, A, E> toEntity;

    public static <E, A> Builder<E, A> of(Class<E> entityClass, Class<A> attributeClass, String name) {
        return new Builder<>(entityClass, attributeClass, name);
    }

    public static <E> Builder<E, Instant> ofInstant(Class<E> entityClass, String name) {
        return new Builder<>(entityClass, Instant.class, name);
    }

    public static <E> Builder<E, String> ofString(Class<E> entityClass, String name) {
        return new Builder<>(entityClass, String.class, name);
    }

    public static <E> Builder<E, BigDecimal> ofBigDecimal(Class<E> entityClass, String name) {
        return new Builder<>(entityClass, BigDecimal.class, name);
    }

    public static class Builder<E, A> {
        // Required parameters
        private final Class<E> entityClass;
        private final Class<A> attributeClass;
        private final String name;
        private Function<E, A> fromEntity;
        private BiFunction<E, A, E> toEntity;

        // Optional parameters - initialized to default values (these are only here in a single location)
        private boolean primary = false;

        public Builder(Class<E> entityClass, Class<A> attributeClass, String name) {
            this.entityClass = entityClass;
            this.attributeClass = attributeClass;
            this.name = name;
        }

        public Builder<E, A> primary() {
            primary = true;
            return this;
        }

        public Builder<E, A> get(Function<E, A> fromEntity) {
            this.fromEntity = fromEntity;
            return this;
        }

        public Builder<E, A> updatedEntity(BiFunction<E, A, E> toEntity) {
            this.toEntity = toEntity;
            return this;
        }

        public Builder<E, A> set(BiConsumer<E, A> setToEntity) {
            return updatedEntity((e, a) -> { setToEntity.accept(e, a); return e; });
        }

        public Attr build() {
            Attr attr = new Attr(this);
            // Possible validations here (checks on fields)...
            if (toEntity == null) {
                throw new IllegalStateException("updatedEntity mapping is missing");
            }
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
        toEntity = builder.toEntity;
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

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
    public E entityWithAttribute(E entity, AttributeSource attributeSource, String attributeName) {
        A value = attributeSource.get(attributeClass, attributeName);
        return toEntity.apply(entity, value);
    }

    public boolean isPrimary() {
        return primary;
    }

    public Function<E, A> getFromEntity() {
        return fromEntity;
    }

    public BiFunction<E, A, E> getToEntity() {
        return toEntity;
    }
}
