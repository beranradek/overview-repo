package cz.etn.overview.filter;

/**
 * Condition grouping two nested conditions using OR operator.
 * @author Radek Beran
 */
public final class OrCondition extends Condition2 {

    public OrCondition(Condition firstCondition, Condition secondCondition) {
        super(firstCondition, secondCondition);
    }
}
