package cz.etn.overview.sql.filter;

import cz.etn.overview.filter.*;
import cz.etn.overview.common.funs.CollectionFuns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds SQL conditions from various condition types.
 * @author Radek Beran
 */
public class SqlConditionBuilder {

    public static String LIKE_WITH_PLACEHOLDER = "LIKE CONCAT('%', ?, '%')";

    public SqlCondition build(Condition condition, Function<Object, Object> valueToDbSupportedValue) {
        SqlCondition sqlCondition = null;
        if (condition instanceof SqlCondition) {
            sqlCondition = (SqlCondition)condition;
        } else if (condition instanceof EqCondition) {
            EqCondition c = (EqCondition)condition;
            if (c.getValue() == null) {
                sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " IS NULL", CollectionFuns.EMPTY_OBJECT_LIST);
            } else {
                sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " = ?", CollectionFuns.singleValueList(valueToDbSupportedValue.apply(c.getValue())));
            }
        } else if (condition instanceof LtCondition) {
            LtCondition c = (LtCondition)condition;
            sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " < ?", CollectionFuns.singleValueList(valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof LteCondition) {
            LteCondition c = (LteCondition)condition;
            sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " <= ?", CollectionFuns.singleValueList(valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof GtCondition) {
            GtCondition c = (GtCondition)condition;
            sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " > ?", CollectionFuns.singleValueList(valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof GteCondition) {
            GteCondition c = (GteCondition)condition;
            sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " >= ?", CollectionFuns.singleValueList(valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof EqAttributesCondition) {
            EqAttributesCondition c = (EqAttributesCondition)condition;
            sqlCondition = new SqlCondition(c.getFirstAttribute().getNameFull() + " = " + c.getSecondAttribute().getNameFull(), CollectionFuns.EMPTY_OBJECT_LIST);
        } else if (condition instanceof ContainsCondition) {
            ContainsCondition c = (ContainsCondition)condition;
            List<Object> values = new ArrayList<>();
            values.add(valueToDbSupportedValue.apply(c.getValue()));
            sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " " + LIKE_WITH_PLACEHOLDER, values);
        } else if (condition instanceof InCondition) {
            InCondition c = (InCondition)condition;
            if (c.getValues() != null && !c.getValues().isEmpty()) {
                String[] placeholders = new String[c.getValues().size()];
                Arrays.fill(placeholders, "?");
                String commaSeparatedPlaceholders = CollectionFuns.join(Arrays.asList(placeholders), ", ");
                Stream<Object> valuesStream = c.getValues().stream().map(v -> valueToDbSupportedValue.apply(v));
                List<Object> valuesForPlaceholders = valuesStream.collect(Collectors.toList());
                sqlCondition = new SqlCondition(c.getAttribute().getNameFull() + " IN (" + commaSeparatedPlaceholders + ")", valuesForPlaceholders);
            } else {
                // empty values for IN, value of attribute is certainly not among empty values
                sqlCondition = new SqlCondition("1 = 0", CollectionFuns.EMPTY_OBJECT_LIST);
            }

        } else {
            throw new IllegalStateException("Condition " + condition + " is not supported");
        }
        return sqlCondition;
    }
}
