package cz.etn.overview.mapper;

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
public class Attribute<E, A> implements AttributeMapping<E, A> {
    private final Class<A> cls;
    private final String name;
    private final boolean primary;
    private final Function<E, A> fromEntity;
    private final BiFunction<E, A, E> toEntity;

    public static <T, U> Builder<T, U> of(Class<U> cls, String name) {
        return new Builder<T, U>(cls, name);
    }

    public static class Builder<E, A> {
        // Required parameters
        private final Class<A> cls;
        private final String name;
        private Function<E, A> fromEntity;
        private BiFunction<E, A, E> toEntity;

        // Optional parameters - initialized to default values (these are only here in a single location)
        private boolean primary = false;

        public Builder(Class<A> cls, String name) {
            this.cls = cls;
            this.name = name;
        }

        public Builder primary() {
            primary = true;
            return this;
        }

        public Builder fromEntity(Function<E, A> fromEntity) {
            this.fromEntity = fromEntity;
            return this;
        }

        public Builder toEntity(BiFunction<E, A, E> toEntity) {
            this.toEntity = toEntity;
            return this;
        }

        public Attribute build() {
            Attribute attr = new Attribute(this);
            // Possible validations here (checks on fields)...
            if (toEntity == null) {
                throw new IllegalStateException("toEntity mapping is missing");
            }
            if (fromEntity == null) {
                throw new IllegalStateException("fromEntity mapping is missing");
            }
            return attr;
        }
    }

    private Attribute(Builder builder) {
        cls = builder.cls;
        name = builder.name;
        primary = builder.primary;
        fromEntity = builder.fromEntity;
        toEntity = builder.toEntity;
    }

    public Class<A> getCls() {
        return cls;
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
        A value = attributeSource.get(cls, attributeName);
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
