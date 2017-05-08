package cz.etn.overview.mapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
    private final BiFunction<E, A, E> toEntity;
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

    /**
     * Copy constructor.
     */
    public Attr(Attr source, String namePrefix) {
        this.entityClass = source.entityClass;
        this.attributeClass = source.attributeClass;
        this.name = source.name;
        this.primary = source.primary;
        this.fromEntity = source.fromEntity;
        this.toEntity = source.toEntity;
        this.namePrefix = namePrefix;
        this.maxLength = source.maxLength;
    }

    public static class Builder<E, A> {
        // Required parameters
        private final Class<E> entityClass;
        private final Class<A> attributeClass;
        private final String name;
        private Function<E, A> fromEntity;
        private BiFunction<E, A, E> toEntity;
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

        public Builder<E, A> namePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
            return this;
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
        namePrefix = builder.namePrefix;
        maxLength = builder.maxLength;
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

    public BiFunction<E, A, E> getToEntity() {
        return toEntity;
    }

    @Override
    public String getNamePrefix() {
        return namePrefix;
    }

    @Override
    public Attribute<E, A> withNamePrefix(String namePrefix) {
        return new Attr(this, namePrefix);
    }
}
