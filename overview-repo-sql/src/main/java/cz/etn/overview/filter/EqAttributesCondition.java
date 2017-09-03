package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute equals to another attribute.
 * @author Radek Beran
 */
public final class EqAttributesCondition<T, U, A, B> implements Condition {

    private final Attribute<T, A> firstAttribute;

    private final Attribute<U, B> secondAttribute;

    public EqAttributesCondition(Attribute<T, A> firstAttribute, Attribute<U, B> secondAttribute) {
        this.firstAttribute = firstAttribute;
        this.secondAttribute = secondAttribute;
    }

    public Attribute<T, A> getFirstAttribute() {
        return firstAttribute;
    }

    public Attribute<U, B> getSecondAttribute() {
        return secondAttribute;
    }
}
