package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute is greater than or equal to value.
 * @author Radek Beran
 */
public final class GteCondition<T, A> implements Condition {

    private final Attribute<T, A> attribute;

    private final A value;

    public GteCondition(Attribute<T, A> attribute, A value) {
        this.attribute = attribute;
        this.value = value;
    }

    public Attribute<T, A> getAttribute() {
        return attribute;
    }

    public A getValue() {
        return value;
    }
}
