package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

import java.util.List;

/**
 * Attribute has one of the specified values.
 * @author Radek Beran
 */
public final class InCondition<T, A> implements Condition {

    private final Attribute<T, A> attribute;

    private final List<A> values;

    public InCondition(Attribute<T, A> attribute, List<A> values) {
        this.attribute = attribute;
        this.values = values;
    }

    public Attribute<T, A> getAttribute() {
        return attribute;
    }

    public List<A> getValues() {
        return values;
    }
}
