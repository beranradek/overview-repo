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

import org.xbery.overview.filter.Condition;
import org.xbery.overview.common.funs.CollectionFuns;

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
