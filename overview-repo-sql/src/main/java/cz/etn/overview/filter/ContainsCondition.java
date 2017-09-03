package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute's value contains some substring.
 * @author Radek Beran
 */
public final class ContainsCondition<T, A> implements Condition {

    private final Attribute<T, A> attribute;

    private final A value;

    public ContainsCondition(Attribute<T, A> attribute, A value) {
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
