package cz.etn.overview.mapper;

import java.util.*;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<E> implements AbstractEntityMapper<E> {

    private final Map<String, Attribute<?, ?>> attributesByNames;

    public DynamicEntityMapper() {
        attributesByNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attributeBuilder filled builder of new attribute (attribute must not be already registered)
     */
    public synchronized <U, V> Attribute<U, V> add(Attr.Builder<U, V> attributeBuilder) {
        return add(attributeBuilder.build());
    }

    /**
     * Registers new attribute.
     * This method is an instance method (not a static one) so the mapper implementations can inject various components.
     * @param attribute new attribute (attribute must not be already registered)
     */
    public synchronized <U, V> Attribute<U, V> add(Attribute<U, V> attribute) {
        String name = attribute.getName();
        if (attributesByNames.containsKey(name)) {
            throw new IllegalStateException("Attribute " + name + " is already registered");
        }

        attributesByNames.put(name, attribute);
        return attribute;
    }

    @Override
    public Attribute<E, ?>[] getAttributes() {
        return attributesByNames.values().toArray(new Attribute[0]);
    }
}
