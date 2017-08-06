package cz.etn.overview.sql.filter;

import cz.etn.overview.filter.Condition;
import cz.etn.overview.common.funs.CollectionFuns;

import java.util.List;

/**
 * Condition for SQL WHERE or ON clause.
 * @author Radek Beran
 */
public final class SqlCondition implements Condition {
    private final String conditionWithPlaceholders;
    private final List<Object> values;

    public SqlCondition(String conditionWithPlaceholders, List<Object> values) {
        this.conditionWithPlaceholders = conditionWithPlaceholders;
        this.values = values;
    }

    public SqlCondition(String conditionWithPlaceholders) {
        this(conditionWithPlaceholders, CollectionFuns.EMPTY_OBJECT_LIST);
    }

    public String getConditionWithPlaceholders() {
        return conditionWithPlaceholders;
    }

    public List<Object> getValues() {
        return values;
    }
}
