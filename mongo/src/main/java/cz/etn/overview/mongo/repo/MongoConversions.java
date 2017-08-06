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
package cz.etn.overview.mongo.repo;

import org.bson.Document;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Utility methods for converting between Mongo Document and Java classes.
 *
 * @author Radek Beran
 * @author Zdenek Straka
 * @author Vojtech Ruschka
 */
public class MongoConversions {

    /**
     * Converts Mongo Document to Java object of type T.
     * Child field must be Mongo Document type, otherwise exception is thrown.
     * Caller must check parent to be not null.
     *
     * @param parent Parent document. Must be not null.
     * @param fld Field to extract from parent document
     * @param conv Conversion function between Document and result type T
     * @return Converted object. Null if fld is not found in parent.
     */
    public static <T> T asObject(Document parent, String fld, Function<Document, T> conv) {
        Objects.requireNonNull(parent, "parent document must be set");
        Document child = parent.get(fld, Document.class);
        if (child == null) {
            return null;
        }

        return conv.apply(child);
    }

//    /**
//     * Converts list of Mongo Document to list of Java objects.
//     * It is NOT useful for converting list of string and etc.
//     *
//     * @param parent Parent document
//     * @param fld Field from parent document to extract
//     * @param conv Conversion function between document and Java class in list
//     * @return List of converted documents
//     */
//    public static <T> List<T> asObjectList(Document parent, String fld, Function<Document, T> conv) {
//        Objects.requireNonNull(parent, "parent document must be set");
//        List<Document> docs = asDocList(parent, fld);
//        if (docs == null){
//            return null;
//        }
//        return new ArrayList<T>(FluentIterable.from(docs)
//            .filter(Predicates.notNull())
//            .transform(d -> conv.apply(d))
//            .filter(Predicates.notNull())
//            .toList());
//    }

    @SuppressWarnings("unchecked")
    public static Class<List<String>> listStringClass() {
        return (Class<List<String>>) (Class<?>) List.class;
    }

//    public static List<Document> asDocList(Document parent, String fld) {
//        return CollectionTools.safeCastList(Document.class, parent.get(fld, listClass()));
//    }
//
//    public static List<String> asStringList(Document parent, String fld) {
//        return CollectionTools.safeCastList(String.class, parent.get(fld, listClass()));
//    }
//
//    public static List<Integer> asIntegerList(Document parent, String fld) {
//        return CollectionTools.safeCastList(Integer.class, parent.get(fld, listClass()));
//    }
//
//    public static <T> List<T> asList(Class<T> itemClass, Document doc, String fld) {
//        return CollectionTools.safeCastList(itemClass, doc.get(fld, listClass()));
//    }

    public static BigDecimal asBigDecimal(String str) {
        return as(str, s -> new BigDecimal(s));
    }
    public static BigDecimal asDecimalFromDouble(Double d) {
        return (d == null) ? null : BigDecimal.valueOf(d.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public static <T extends Enum<T>> T asEnum(String str, Class<T> cls) {
        return as(str, s -> Enum.valueOf(cls, s));
    }

//    public static <T extends Enum<T>> List<T> asEnumList(Document parent, String fld, Class<T> cls){
//        List<String> li = asStringList(parent, fld);
//        if (li == null) {
//            return null;
//        }
//        return li.stream().map(e -> asEnum(e, cls)).collect(Collectors.toList());
//    }

    public static <T> Map<String, T> asMap(Document document) {
        if (document == null) return null;

        Map<String, T> m = new HashMap<String, T>();

        for (Entry<String, Object> entry : document.entrySet()) {
            @SuppressWarnings("unchecked")
            T val = (T) entry.getValue();
            m.put(entry.getKey(), val);
        }
        return m;
    }

    public static Document asDoc(Map<String, String> map) {
        if (map == null) return null;
        Document doc = new Document();
        for (Entry<String, String> entry : map.entrySet()) {
            doc.put(entry.getKey(), entry.getValue());
        }
        return doc;
    }

    public static <T> Map<String, T> asMap(Document parent, String fld) {
        if (parent == null){
            return null;
        }
        return asMap(parent.get(fld, Document.class));
    }


    public static Instant asInstant(Date dt) {
        if (dt == null) return null;
        return dt.toInstant();
    }

    public static Instant asFutureInstant(Date dt, Instant now) {
        if (dt == null) return null;
        Instant res = dt.toInstant();
        if (!res.isAfter(now))
            return null;
        return res;
    }

    public static Year asYear(String year) {
        return as(year, y -> Year.parse(y));
    }


    public static Duration asDuration(String duration) {
        return as(duration, d -> Duration.parse(d));
    }


    public static LocalDate asLocalDate(String localDate) {
        return as(localDate, ld -> DateTimeFormatter.ISO_LOCAL_DATE.parse(ld, LocalDate::from));
    }

    public static <T> T as(String s, Function<String, T> parser) {
        if (s == null) return null;
        return parser.apply(s);
    }

    @SuppressWarnings("unchecked")
    private static Class<List<?>> listClass() {
        return (Class<List<?>>) (Class<?>) List.class;
    }

    // TODO RBe: the following methods are used for updates, not for reading -> move to another class?

    public static Date storeDate(Instant time) {
        return (time == null) ? null : Date.from(time);
    }

    public static String storeDecimal(BigDecimal num) {
        return (num == null) ? null : num.toString();
    }

    public static Double storeDecimalAsDouble(BigDecimal num) {
        return (num == null) ? null : Double.valueOf(num.doubleValue());
    }

    public static String storeEnum(Enum<?> val) {
        return (val == null) ? null : val.name();
    }
}
