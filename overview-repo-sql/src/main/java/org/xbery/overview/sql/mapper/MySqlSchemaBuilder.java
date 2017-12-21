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

import org.xbery.overview.mapper.Attr;
import org.xbery.overview.mapper.Attribute;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Constructs base of SQL DDL commands based on entity attributes.
 * @author Radek Beran
 */
public class MySqlSchemaBuilder {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String UNKNOWN = "UNKNOWN";

    public <E> String composeCreateTableSQL(String tableName, List<Attribute<E, ?>> attributes) {
        StringBuilder sb = new StringBuilder();
        if (attributes != null && attributes.size() > 0) {
            Optional<String> primaryAttrNameOpt = getPrimaryAttributeName(attributes);
            sb.append("CREATE TABLE IF NOT EXISTS `" + tableName + "` (" + NEW_LINE);
            boolean first = true;
            for (Attribute<E, ?> attr : attributes) {
                if (first) {
                    first = false;
                } else {
                    sb.append("," + NEW_LINE);
                }
                String sqlTypeName = getSqlTypeName(attr);
                sb.append("\t`" + attr.getName() + "` " + sqlTypeName);
                if (attr.isPrimary()) {
                    sb.append(" NOT NULL");
                    if (isNumber(attr)) {
                        sb.append(" AUTO_INCREMENT");
                    }
                }
            }
            primaryAttrNameOpt.map (primaryAttrName -> {
                sb.append("," + NEW_LINE);
                sb.append("\tPRIMARY KEY (`" + primaryAttrName + "`)");
                return null;
            });
            sb.append(NEW_LINE + ");" + NEW_LINE);
        }
        return sb.toString();
    }

    protected <E> Optional<String> getPrimaryAttributeName(List<Attribute<E, ?>> attributes) {
        Optional<String> name = Optional.empty();
        if (attributes != null) {
            for (Attribute<?, ?> attr : attributes) {
                if (attr.isPrimary()) {
                    name = Optional.ofNullable(attr.getName());
                    break;
                }
            }
        }
        return name;
    }

    protected <E, A> String getSqlTypeName(Attribute<E, A> attribute) {
        String typeName = UNKNOWN;
        if (attribute instanceof Attr) {
            Attr attr = (Attr)attribute;
            String maxLength = attribute.getMaxLength().map(l -> "" + l).orElse(UNKNOWN);
            Class<A> attrClass = attr.getAttributeClass();
            if (String.class.isAssignableFrom(attrClass)) {
                typeName = "VARCHAR(" + maxLength + ")";
            } else if (Byte.class.isAssignableFrom(attrClass) || Integer.class.isAssignableFrom(attrClass) || Long.class.isAssignableFrom(attrClass)) {
                typeName = "INT(" + maxLength + ")";
            } else if (Instant.class.isAssignableFrom(attrClass) || Date.class.isAssignableFrom(attrClass)) {
                typeName = "DATETIME";
            } else if (BigDecimal.class.isAssignableFrom(attrClass)) {
                typeName = "DECIMAL(" + maxLength + ", " + UNKNOWN + ")";
            }
        }
        return typeName;
    }

    protected <E, A> boolean isNumber(Attribute<E, A> attribute) {
        boolean number = false;
        if (attribute instanceof Attr) {
            Attr attr = (Attr)attribute;
            Class<A> attrClass = attr.getAttributeClass();
            number = Byte.class.isAssignableFrom(attrClass) || Integer.class.isAssignableFrom(attrClass) || Long.class.isAssignableFrom(attrClass) || BigDecimal.class.isAssignableFrom(attrClass);
        }
        return number;
    }
}
