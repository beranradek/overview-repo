package cz.etn.overview.filter;

import java.util.Objects;

/**
 * Condition grouping two nested conditions using an operator.
 * @author Radek Beran
 */
public abstract class Condition2 implements Condition {

    private final Condition firstCondition;
    private final Condition secondCondition;

    public Condition2(Condition firstCondition, Condition secondCondition) {
        Objects.requireNonNull(firstCondition, "First condition must be specified");
        Objects.requireNonNull(secondCondition, "Second condition must be specified");
        this.firstCondition = firstCondition;
        this.secondCondition = secondCondition;
    }

    public Condition getFirstCondition() {
        return firstCondition;
    }

    public Condition getSecondCondition() {
        return secondCondition;
    }
}
