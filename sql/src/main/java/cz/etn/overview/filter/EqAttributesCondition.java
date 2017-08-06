package cz.etn.overview.filter;

import cz.etn.overview.mapper.Attribute;

/**
 * Attribute equals to another attribute.
 * @author Radek Beran
 */
public final class EqAttributesCondition implements Condition {

    private final Attribute<?, ?> firstAttribute;

    private final Attribute<?, ?> secondAttribute;

    public EqAttributesCondition(Attribute<?, ?> firstAttribute, Attribute<?, ?> secondAttribute) {
        this.firstAttribute = firstAttribute;
        this.secondAttribute = secondAttribute;
    }

    public Attribute<?, ?> getFirstAttribute() {
        return firstAttribute;
    }

    public Attribute<?, ?> getSecondAttribute() {
        return secondAttribute;
    }
}
