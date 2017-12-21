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
package org.xbery.overview.mongo.filter;

import com.mongodb.Function;
import com.mongodb.client.model.Filters;
import org.xbery.overview.filter.*;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Builds Mongo conditions from various condition types.
 * @author Radek Beran
 */
public class MongoConditionBuilder {

    public MongoCondition build(Condition condition, Function<Object, Object> valueToDbSupportedValue) {
        MongoCondition mongoCondition = null;
        if (condition instanceof MongoCondition) {
            mongoCondition = (MongoCondition)condition;
        } else if (condition instanceof EqCondition) {
            EqCondition c = (EqCondition)condition;
            mongoCondition = new MongoCondition(Filters.eq(c.getAttribute().getName(), valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof LtCondition) {
            LtCondition c = (LtCondition)condition;
            mongoCondition = new MongoCondition(Filters.lt(c.getAttribute().getName(), valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof LteCondition) {
            LteCondition c = (LteCondition)condition;
            mongoCondition = new MongoCondition(Filters.lte(c.getAttribute().getName(), valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof GtCondition) {
            GtCondition c = (GtCondition)condition;
            mongoCondition = new MongoCondition(Filters.gt(c.getAttribute().getName(), valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof GteCondition) {
            GteCondition c = (GteCondition)condition;
            mongoCondition = new MongoCondition(Filters.gte(c.getAttribute().getName(), valueToDbSupportedValue.apply(c.getValue())));
        } else if (condition instanceof EqAttributesCondition) {
            EqAttributesCondition c = (EqAttributesCondition)condition;
            // This isn't very efficient since $where doesn't take advantage of indexing. An alternative would be to
            // have a boolean that you set when inserting/updating that checks for seller/author equality.
            // See https://www.codeschool.com/discuss/t/how-to-query-based-on-the-variable-value-stored-in-another-field/20979/3
            mongoCondition = new MongoCondition(Filters.where("this." + c.getFirstAttribute().getName() + " == this." + c.getSecondAttribute().getName()));
        } else if (condition instanceof ContainsCondition) {
            ContainsCondition c = (ContainsCondition)condition;
            mongoCondition = new MongoCondition(Filters.regex(c.getAttribute().getName(), ".*" + Pattern.quote("" + valueToDbSupportedValue.apply(c.getValue())) + ".*"));
        } else if (condition instanceof InCondition) {
            InCondition c = (InCondition)condition;
            if (c.getValues() != null && !c.getValues().isEmpty()) {
                mongoCondition = new MongoCondition(Filters.in(c.getAttribute().getName(), c.getValues().stream().map(v -> valueToDbSupportedValue.apply(v)).collect(Collectors.toList())));
            } else {
                // empty values for IN, value of attribute is certainly not among empty values
                mongoCondition = new MongoCondition(Filters.where("1 == 0"));
            }
        } else if (condition instanceof OrCondition) {
            OrCondition c = (OrCondition)condition;
            mongoCondition = new MongoCondition(Filters.or(build(c.getFirstCondition(), valueToDbSupportedValue).getCondition(), build(c.getSecondCondition(), valueToDbSupportedValue).getCondition()));
        } else if (condition instanceof AndCondition) {
            AndCondition c = (AndCondition)condition;
            mongoCondition = new MongoCondition(Filters.and(build(c.getFirstCondition(), valueToDbSupportedValue).getCondition(), build(c.getSecondCondition(), valueToDbSupportedValue).getCondition()));
        } else {
            throw new IllegalStateException("Condition " + condition + " is not supported");
        }
        return mongoCondition;
    }
}
