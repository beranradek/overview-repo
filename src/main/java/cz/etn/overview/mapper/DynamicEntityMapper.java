package cz.etn.overview.mapper;

import cz.etn.overview.Filter;
import cz.etn.overview.common.Pair;
import cz.etn.overview.repo.FilterCondition;
import cz.etn.overview.repo.join.JoinType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<T, F extends Filter> implements AbstractEntityMapper<T, F> {

    private final Map<String, Attribute<T, ?>> attributesByNames;

    public DynamicEntityMapper() {
        attributesByNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attributeBuilder filled builder of new attribute (attribute must not be already registered)
     */
    public synchronized <A> Attribute<T, A> add(Attr.Builder<T, A> attributeBuilder) {
        if (attributeBuilder.namePrefix == null) {
            // name prefix not set yet
            attributeBuilder = attributeBuilder.namePrefix(getNamePrefix());
        }
        return add(attributeBuilder.build());
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attribute new attribute (attribute must not be already registered)
     */
    public synchronized <A> Attribute<T, A> add(Attribute<T, A> attribute) {
        String name = attribute.getName();
        if (attributesByNames.containsKey(name)) {
            throw new IllegalStateException("Attribute " + name + " is already registered");
        }

        if (attribute.getNamePrefix() == null) {
            // name prefix not set yet
            attribute = attribute.withNamePrefix(getNamePrefix());
        }

        attributesByNames.put(name, attribute);
        return attribute;
    }

    @Override
    public Attribute<T, ?>[] getAttributes() {
        return attributesByNames.values().toArray(new Attribute[0]);
    }

    /**
     * Creates entity mapper for joined entities.
     * @param secondMapper mapper of second entity to join with
     * @param onConditions condition(s) for joining the tables
     * @param composeEntity function combining joined entities together to resulting entity
     * @param decomposeFilter function returning partial filters for first and second joined entity
     * @param join type of join operation
     * @param <U> type of second entity to join with
     * @param <G> type of second entity filter
     * @param <V> type of resulting entity representing joined records, this can be also the same type as T or U
     * @param <H> type of resulting entity filter
     * @return mapper for joined entities
     */
    public <U, G extends Filter, V, H extends Filter> DynamicEntityMapper<V, H> join(DynamicEntityMapper<U, G> secondMapper, List<FilterCondition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter, JoinType join) {
        final DynamicEntityMapper<T, F> firstMapper = this;
        return new DynamicEntityMapper<V, H>() {
            @Override
            public V createEntity() {
                return composeEntity.apply(firstMapper.createEntity(), secondMapper.createEntity());
            }

            @Override
            public V buildEntity(AttributeSource attributeSource, String aliasPrefix) {
                T firstEntity = firstMapper.buildEntity(attributeSource, aliasPrefix);
                U secondEntity = secondMapper.buildEntity(attributeSource, aliasPrefix);
                return composeEntity.apply(firstEntity, secondEntity);
            }

            @Override
            public String getDataSet() {
                // TODO RBe: Implement with X (LEFT|RIGHT|INNER) JOIN Y ON conditions
                return null;
            }

            @Override
            public List<FilterCondition> composeFilterConditions(H filter) {
                List<FilterCondition> conditions = new ArrayList<>();
                Pair<F, G> filters = decomposeFilter.apply(filter);
                if (filters.getFirst() != null) {
                    conditions.addAll(firstMapper.composeFilterConditions(filters.getFirst()));
                }
                if (filters.getSecond() != null) {
                    conditions.addAll(secondMapper.composeFilterConditions(filters.getSecond()));
                }
                return conditions;
            }
        };
    }

    public <U, G extends Filter, V, H extends F> DynamicEntityMapper<V, H> innerJoin(DynamicEntityMapper<U, G> secondMapper, List<FilterCondition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
        return join(secondMapper, onConditions, composeEntity, decomposeFilter, JoinType.INNER);
    }

    protected String getNamePrefix() {
        return getDataSet();
    }
}
