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
package cz.etn.overview.mongo.mapper;

import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mongo.repo.MongoConversions;
import org.bson.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

/**
 * {@link Document} attribute source.
 * @author Radek Beran
 */
public class DocumentAttributeSource implements AttributeSource {

    private final Document doc;

    public DocumentAttributeSource(Document doc) {
        this.doc = doc;
    }

    public Document getDocument() {
        return doc;
    }

    @Override
    public <A> A get(Class<A> cls, String attributeName) {
        Object value = null;
        if (cls.isAssignableFrom(Boolean.class)) {
            value = getBoolean(attributeName);
        } else if (cls.isAssignableFrom(Byte.class)) {
            value = getByte(attributeName);
        } else if (cls.isAssignableFrom(Integer.class)) {
            value = getInteger(attributeName);
        } else if (cls.isAssignableFrom(Long.class)) {
            value = getLong(attributeName);
        } else if (cls.isAssignableFrom(Float.class)) {
            value = getFloat(attributeName);
        } else if (cls.isAssignableFrom(Double.class)) {
            value = getDouble(attributeName);
        } else if (cls.isAssignableFrom(Date.class)) {
            value = getDate(attributeName);
        } else if (cls.isAssignableFrom(Instant.class)) {
            value = getInstant(attributeName);
        } else if (cls.isAssignableFrom(BigDecimal.class)) {
            value = getBigDecimal(attributeName);
        } else if (cls.isAssignableFrom(String.class)) {
            value = getString(attributeName);
        }
        return value != null ? cls.cast(value) : null;
    }

    protected Long getLong(String attributeName) {
        return doc.getLong(attributeName);
    }

    protected Integer getInteger(String attributeName) {
        return doc.getInteger(attributeName);
    }

    protected String getString(String attributeName) {
        return doc.getString(attributeName);
    }

    protected Instant getInstant(String attributeName) {
        Date date = doc.getDate(attributeName);
        return date != null ? date.toInstant() : null;
    }

    protected BigDecimal getBigDecimal(String attributeName) {
        return MongoConversions.asBigDecimal(doc.getString(attributeName));
    }

    protected Boolean getBoolean(String attributeName) {
        return doc.getBoolean(attributeName);
    }

    protected Byte getByte(String attributeName) {
        Integer i = doc.getInteger(attributeName);
        return i != null ? Byte.valueOf(i.byteValue()) : null;
    }

    protected Date getDate(String attributeName) {
        return doc.getDate(attributeName);
    }

    protected Float getFloat(String attributeName) {
        Double d = doc.getDouble(attributeName);
        return d != null ? Float.valueOf(d.floatValue()) : null;
    }

    protected Double getDouble(String attributeName) {
        return doc.getDouble(attributeName);
    }
}
