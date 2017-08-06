package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute's value contains some substring.
 * @author Radek Beran
 */
public final class ContainsCondition implements Condition {

    private final Attribute<?, ?> attribute;

    private final Object value;

    public ContainsCondition(Attribute<?, ?> attribute, Object value) {
        this.attribute = attribute;
        this.value = value;
    }

    public Attribute<?, ?> getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }
}
