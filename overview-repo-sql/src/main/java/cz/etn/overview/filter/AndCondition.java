package cz.etn.overview.filter;

/**
 * Condition grouping two nested conditions using AND operator.
 * @author Radek Beran
 */
public final class AndCondition extends Condition2 {

    public AndCondition(Condition firstCondition, Condition secondCondition) {
        super(firstCondition, secondCondition);
    }
}
