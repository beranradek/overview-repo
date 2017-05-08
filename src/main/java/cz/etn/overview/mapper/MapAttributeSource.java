package cz.etn.overview.mapper;

import java.util.Map;

/**
 * Attribute source wrapping a map.
 * @author Radek Beran
 */
public class MapAttributeSource implements AttributeSource {

    private final Map<String, Object> namesToValues;

    public MapAttributeSource(Map<String, Object> namesToValues) {
        this.namesToValues = namesToValues;
    }

    @Override
    public <A> A get(Class<A> cls, String attributeName) {
        return cls.cast(namesToValues.get(attributeName));
    }
}
