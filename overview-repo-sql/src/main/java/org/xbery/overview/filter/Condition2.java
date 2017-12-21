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
package org.xbery.overview.filter;

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
