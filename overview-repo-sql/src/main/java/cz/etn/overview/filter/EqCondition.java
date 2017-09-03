package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute equals to value. Value can be {@code null} to create condition on null value.
 * @author Radek Beran
 */
public final class EqCondition<T, A> implements Condition {

    private final Attribute<T, A> attribute;

    private final A value;

    public EqCondition(Attribute<T, A> attribute, A value) {
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
