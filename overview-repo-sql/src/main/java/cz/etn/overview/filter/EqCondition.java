package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute equals to value. Value can be {@code null} to create condition on null value.
 * @author Radek Beran
 */
public final class EqCondition implements Condition {

    private final Attribute<?, ?> attribute;

    private final Object value;

    public EqCondition(Attribute<?, ?> attribute, Object value) {
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
