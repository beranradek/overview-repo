package cz.etn.overview.mapper;

import java.util.*;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<E> implements AbstractEntityMapper<E> {

    private Map<String, AttributeMapping<E, ?>> attributesByNames;

    public DynamicEntityMapper() {
        attributesByNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * @param attribute new attribute (must not be already registered)
     */
    public synchronized <A> AttributeMapping<E, A> add(AttributeMapping<E, A> attribute) {
        String name = attribute.getName();
        if (attributesByNames.containsKey(name)) {
            throw new IllegalStateException("Attribute " + name + " is already registered");
        }

        attributesByNames.put(name, attribute);
        return attribute;
    }

    @Override
    public AttributeMapping<E, ?>[] getAttributeMappings() {
        return attributesByNames.values().toArray(new AttributeMapping[0]);
    }
}
