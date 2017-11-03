package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute is less than or equal to value.
 * @author Radek Beran
 */
public final class LteCondition<T, A> implements Condition {

    private final Attribute<T, A> attribute;

    private final A value;

    public LteCondition(Attribute<T, A> attribute, A value) {
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
