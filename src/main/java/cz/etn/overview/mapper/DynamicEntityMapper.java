package cz.etn.overview.mapper;

import java.util.*;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<T> implements AbstractEntityMapper<T> {

    private Map<String, AttributeMapping<T>> attributesByNames;

    public DynamicEntityMapper() {
        this.attributesByNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * @param attribute new attribute (must not be already registered)
     */
    public synchronized void add(AttributeMapping<T> attribute) {
        String name = attribute.getAttributeName();
        if (this.attributesByNames.containsKey(name)) {
            throw new IllegalStateException("Attribute " + name + " is already registered");
        }

        this.attributesByNames.put(name, attribute);
    }

    @Override
    public AttributeMapping<T>[] getAttributeMappings() {
        return this.attributesByNames.values().toArray(new AttributeMapping[0]);
    }
}
