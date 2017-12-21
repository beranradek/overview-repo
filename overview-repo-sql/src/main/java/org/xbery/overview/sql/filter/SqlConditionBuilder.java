/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xbery.overview.sql.filter;

import org.xbery.overview.common.funs.CollectionFuns;
import org.xbery.overview.filter.*;

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

        } else if (condition instanceof OrCondition) {
            sqlCondition = createSqlCondition2((OrCondition)condition, "OR", valueToDbSupportedValue);
        } else if (condition instanceof AndCondition) {
            sqlCondition = createSqlCondition2((AndCondition)condition, "AND", valueToDbSupportedValue);
        } else {
            throw new IllegalStateException("Condition " + condition + " is not supported");
        }
        return sqlCondition;
    }

    private SqlCondition createSqlCondition2(Condition2 condition, String operator, Function<Object, Object> valueToDbSupportedValue) {
        SqlCondition first = build(condition.getFirstCondition(), valueToDbSupportedValue);
        SqlCondition second = build(condition.getSecondCondition(), valueToDbSupportedValue);
        List<Object> values = new ArrayList<>();
        values.addAll(first.getValues());
        values.addAll(second.getValues());
        return new SqlCondition("(" + first.getConditionWithPlaceholders() + " " + operator + " " + second.getConditionWithPlaceholders() + ")", values);
    }
}
