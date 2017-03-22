package cz.etn.overview.mapper;

import java.util.*;

/**
 * Entity mapper with dynamic registration of attributes.
 * @author Radek Beran
 */
public abstract class DynamicEntityMapper<E> implements AbstractEntityMapper<E> {

    private static Map<String, Attribute<?, ?>> attributesByNames;

    static {
        attributesByNames = new LinkedHashMap<>();
    }

    /**
     * Registers new attribute.
     * @param attributeBuilder filled builder of new attribute (attribute must not be already registered)
     */
    public static synchronized <U, V> Attribute<U, V> add(Attr.Builder<U, V> attributeBuilder) {
        return add(attributeBuilder.build());
    }

    /**
     * Registers new attribute.
     * @param attribute new attribute (attribute must not be already registered)
     */
    public static synchronized <U, V> Attribute<U, V> add(Attribute<U, V> attribute) {
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