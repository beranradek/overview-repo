package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

import java.util.List;

/**
 * Attribute has one of the specified values.
 * @author Radek Beran
 */
public final class InCondition implements Condition {

    private final Attribute<?, ?> attribute;

    private final List<Object> values;

    public InCondition(Attribute<?, ?> attribute, List<Object> values) {
        this.attribute = attribute;
        this.values = values;
    }

    public Attribute<?, ?> getAttribute() {
        return attribute;
    }

    public List<Object> getValues() {
        return values;
    }
}
