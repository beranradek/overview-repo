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
package org.xbery.overview.sql.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts type of value to a type supported by database/JDBC driver.
 * @author Radek Beran
 */
public class DbTypeConvertor {

    public List<Object> toDbValues(List<Object> attributeValues) {
        List<Object> result = new ArrayList<>();
        if (attributeValues != null) {
            for (Object v : attributeValues) {
                result.add(toDbValue(v));
            }
        }
        return result;
    }

    public Object toDbValue(Object v) {
        Object valueForDb = null;
        // TODO RBe: Conversion of another data types when not supported their passing to JDBC?
        if (v instanceof Instant) {
            // Note that for e.g. PostgreSQL does not support java.util.Date directly,
            // we need to convert to SQL Timestamp
            valueForDb = new Timestamp(((Instant)v).toEpochMilli());
        } else {
            valueForDb = v;
        }
        return valueForDb;
    }
}
