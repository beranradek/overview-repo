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
