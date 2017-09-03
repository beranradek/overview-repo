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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.Pagination;
import cz.etn.overview.common.Pair;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.mapper.MapAttributeSource;
import cz.etn.overview.mongo.filter.MongoCondition;
import cz.etn.overview.mongo.filter.MongoConditionBuilder;
import cz.etn.overview.mongo.mapper.DocumentAttributeSource;
import cz.etn.overview.repo.AggType;
import cz.etn.overview.repo.Conditions;
import cz.etn.overview.repo.Repository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Basic abstract implementation of {@link Repository} for Mongo DB.
 * @param <T> type of entity
 * @param <K> type of entity key (composed keys are supported)
 * @param <F> type of filter
 * @author Radek Beran
 */
public abstract class AbstractMongoRepository<T, K, F> implements Repository<T, K, F> {

    /**
     * Name of the field which is used by the Mongo DB to store the id.
     */
    public static final String FLD_ID = "_id";

    // TODO RBe: Logging of queries and their parameters
    protected static final Logger log = LoggerFactory.getLogger(AbstractMongoRepository.class);

    public static final Document EMPTY_DOCUMENT = new Document();
    private static final MongoConditionBuilder mongoConditionBuilder = new MongoConditionBuilder();

    @Override
    public T create(T entity, boolean autogenerateKey) {
        Objects.requireNonNull(entity, "Entity should be specified");

        Document doc = updateDocWithEntity(new Document(), entity);
        T createdEntity = entity;
        if (autogenerateKey) {
            String id = generateId();
            K generatedKey = convertGeneratedKey(id);
            doc.append(getIdFieldName(), id);
            createdEntity = entityUpdatedWithId(createdEntity, generatedKey);
        }
        getCollection().insertOne(doc);
        return createdEntity;
    }

    @Override
    public Optional<T> update(T entity) {
        Objects.requireNonNull(entity, "Entity should be specified");

        List<Pair<Attribute<T, ?>, Object>> attributesWithValues = entityToAttributesWithValues(entity);
        final Document res = findOneAndUpdate(getEntityMapper().decomposePrimaryKeyOfEntity(entity), attributesWithValues);
        if (res != null) {
            // Document was found
            return Optional.of(getEntityMapper().buildEntity(new DocumentAttributeSource(res)));
        }
        return Optional.<T>empty();
    }

    @Override
    public int update(K id, List<Pair<Attribute<T, ?>, Object>> attributesWithValues) {
        Objects.requireNonNull(id, "id should be specified");
        final Document res = findOneAndUpdate(getEntityMapper().decomposePrimaryKey(id), attributesWithValues);
        if (res != null) {
            // Document was found
            return 1;
        }
        return 0;
    }

    @Override
    public boolean delete(K id) {
        Objects.requireNonNull(id, "id should be specified");
        Document pkFilter = updateDocWithAttributes(new Document(), getEntityMapper().decomposePrimaryKey(id));
        return getCollection().findOneAndDelete(pkFilter) != null;
    }

    @Override
    public int deleteByFilter(F filter) {
        Objects.requireNonNull(filter, "filter should be specified");
        MongoFilter mongoFilter = conditionsToMongoFilter(getEntityMapper().composeFilterConditions(filter));
        DeleteResult result = getCollection().deleteMany(mongoFilter.getFilter().orElse(EMPTY_DOCUMENT)); // TODO RBe: Test passing of empty filter
        return (int)result.getDeletedCount(); // TODO RBe: Checked overflow typecast like in Guava
    }

    /**
     * Returns aggregated values of given attribute for given filter.
     * @param aggType aggregation type
     * @param resultClass
     * @param attrName
     * @param filter
     * @param entityMapper
     * @param <R>
     * @return
     */
    @Override
    public <R, T, F> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter, EntityMapper<T, F> entityMapper) {
        Objects.requireNonNull(aggType, "aggregation type should be specified");
        Objects.requireNonNull(resultClass, "result class should be specified");
        Objects.requireNonNull(attrName, "attribute name should be specified");

        String aggAttributeAlias = attrName + "_agg";

        throw new UnsupportedOperationException("Not implemented yet");
//        List<R> results = queryWithOverview(
//            aggFunction(aggType, attrName) + " AS " + aggAttributeAlias,
//            filter != null ? entityMapper.composeFilterConditions(filter) : new ArrayList<>(),
//            null,
//            null,
//            as -> as.get(resultClass, aggAttributeAlias));
//        return results != null && !results.isEmpty() ? results.get(0) : null;
    }

    /**
     * Returns aggregated values of given attribute for given filter.
     * @param aggType aggregation type
     * @param resultClass
     * @param attrName
     * @param filter
     * @param <R>
     * @return
     */
    @Override
    public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, F filter) {
        return aggByFilter(aggType, resultClass, attrName, filter, getEntityMapper());
    }

    @Override
    public <T, K, F> Optional<T> findById(K id, EntityMapper<T, F> entityMapper) {
        Document pkFilter = updateDocWithAttributes(new Document(), entityMapper.decomposePrimaryKey(id));
        return CollectionFuns.headOpt(getCollection().find(pkFilter)).map(doc -> entityMapper.buildEntity(new DocumentAttributeSource(doc)));
    }

    @Override
    public <T, F> List<T> findByOverview(final Overview<F> overview, EntityMapper<T, F> entityMapper) {
        List<Condition> filterConditions = overview.getFilter() != null ? entityMapper.composeFilterConditions(overview.getFilter()) : new ArrayList<>();
        return queryWithOverview(entityMapper.getAttributes(), filterConditions, overview.getOrder(), overview.getPagination(), as -> entityMapper.buildEntity(as));
    }

    @Override
    public List<T> findByOverview(final Overview<F> overview) {
        return findByOverview(overview, getEntityMapper());
    }

    protected abstract MongoDatabase getDatabase();

    protected List<T> findByOverview(Overview<F> overview, List<Attribute<T, ?>> projectionAttributes) {
        return queryWithOverview(
            projectionAttributes,
            overview.getFilter() != null ? getEntityMapper().composeFilterConditions(overview.getFilter()) : null,
            overview.getOrder(),
            overview.getPagination(),
            as -> getEntityMapper().buildEntity(as)
        );
    }

    protected List<T> findByFilterConditions(List<Condition> filterConditions, List<Order> ordering) {
        return findByFilterConditions(filterConditions, ordering, getEntityMapper());
    }

    protected <T, F> List<T> findByFilterConditions(List<Condition> filterConditions, List<Order> ordering, EntityMapper<T, F> entityMapper) {
        return queryWithOverview(
            entityMapper.getAttributes(),
            filterConditions,
            ordering,
            null,
            as -> entityMapper.buildEntity(as)
        );
    }

    /**
     * Returns one entity for given unique attribute name and value.
     * @param attribute
     * @param attrValue
     * @param <U>
     * @return
     */
    protected <U> Optional<T> findByAttribute(Attribute<T, U> attribute, U attrValue) {
        Objects.requireNonNull(attribute, "attribute should be specified");
        List<Condition> conditions = new ArrayList<>();
        conditions.add(Conditions.eq(attribute, attrValue));
        return CollectionFuns.headOpt(findByFilterConditions(conditions, createDefaultOrdering()));
    }

    /**
     * Updates given entity with key and returns updated entity.
     * @param entity
     * @param key
     * @return entity updated with given id
     */
    protected T entityUpdatedWithId(T entity, K key) {
        List<Pair<Attribute<T, ?>, Object>> attributesToValues = new ArrayList<>();
        attributesToValues.addAll(getEntityMapper().decomposePrimaryKey(key));

        // Fill in attribute source for binding key values to entity
        Map<String, Object> keyAttrSource = new LinkedHashMap<>();
        for (Pair<Attribute<T, ?>, Object> p : attributesToValues) {
            Attribute<T, ?> attr = p.getFirst();
            Object value = p.getSecond();
            keyAttrSource.put(attr.getName(), value);
        }
        MapAttributeSource attrSource = new MapAttributeSource(keyAttrSource);

        // Binding values of primary key parts to entity
        T updatedEntity = entity;
        for (Pair<Attribute<T, ?>, Object> p : attributesToValues) {
            Attribute<T, ?> attr = p.getFirst();
            updatedEntity = attr.entityWithAttribute(updatedEntity, attrSource, attr.getName());
        }
        return updatedEntity;
    }

    /**
     * Subclasses should override this when the type of key is not compatible with a String and key generating is used.
     * @param generatedId
     * @return
     */
    protected K convertGeneratedKey(String generatedId) {
        return (K)generatedId;
    }

    protected List<Order> createDefaultOrdering() {
        // default ordering by key attributes
        return composeOrderingForPrimaryKey();
    }

    protected List<Order> composeOrderingForPrimaryKey() {
        List<String> names = getEntityMapper().getPrimaryAttributeNames();
        return names.stream().map(name -> new Order(name, false)).collect(Collectors.toList());
    }

    // Custom T type is used, this method should be independent on entity type (can be used to load specific attribute type).
    protected <T> List<T> queryWithOverview(
        List<Attribute<T, ?>> projectionAttributes,
        List<Condition> filterConditions,
        List<Order> ordering,
        Pagination pagination,
        Function<AttributeSource, T> entityBuilder) {

        List<T> results = new ArrayList<>();
        MongoFilter mongoFilter = conditionsToMongoFilter(filterConditions);
        // TODO RBe: Test empty filter passing.
        Bson projection = createProjectionDocument(projectionAttributes);
        List<Order> someOrdering = (ordering == null || ordering.isEmpty()) ? createDefaultOrdering() : ordering;
        Bson sort = createSortDocument(someOrdering);
        MongoCursor<Document> cursor = getCollection()
            .find(mongoFilter.getFilter().orElse(EMPTY_DOCUMENT))
            .projection(projection)
            .sort(sort)
            .skip(pagination.getOffset()).limit(pagination.getLimit()).iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                results.add(entityBuilder.apply(new DocumentAttributeSource(doc)));
            }
        } finally {
            cursor.close();
        }
        return results;
    }

    protected <T> Bson createProjectionDocument(List<Attribute<T, ?>> projectionAttributes) {
        if (projectionAttributes == null) {
            return EMPTY_DOCUMENT;
        }
        return Projections.include(projectionAttributes.stream().map(a -> a.getName()).collect(Collectors.toList()));
    }

    protected Bson createSortDocument(List<Order> ordering) {
        Document sort = new Document();
        if (ordering != null) {
            for (Order order : ordering) {
                sort = sort.append(order.getAttribute(), order.isDesc() ? -1 : 1);
            }
        }
        return sort;
    }

    protected MongoConditionBuilder getConditionBuilder() {
        return mongoConditionBuilder;
    }

    protected String aggFunction(AggType aggType, String attrName) {
        String fun;
        switch (aggType) {
            case COUNT:
                fun = "COUNT(" + attrName + ")";
                break;
            case SUM:
                fun = "SUM(" + attrName + ")";
                break;
            case MIN:
                fun = "MIN(" + attrName + ")";
                break;
            case MAX:
                fun = "MAX(" + attrName + ")";
                break;
            case AVG:
                fun = "AVG(" + attrName + ")";
                break;
            default:
                throw new IllegalArgumentException("Unsupported aggregation type: " + aggType);
        }
        return fun;
    }

    protected MongoCollection<Document> getCollection() {
        return getDatabase().getCollection(getEntityMapper().getDataSet());
    }

    protected Document updateDocWithEntity(Document doc, T entity) {
        return updateDocWithAttributes(doc, entityToAttributesWithValues(entity));
    }

    protected <T> Document updateDocWithAttributes(Document doc, List<Pair<Attribute<T, ?>, Object>> attributesWithValues) {
        for (Pair<Attribute<T, ?>, Object> attrWithValue : attributesWithValues) {
            doc.append(attrWithValue.getFirst().getName(), getDbSupportedAttributeValue(attrWithValue.getSecond()));
        }
        return doc;
    }

    protected List<Pair<Attribute<T, ?>, Object>> entityToAttributesWithValues(T entity) {
        return getEntityMapper().getAttributes().stream()
            .map(a -> new Pair<Attribute<T, ?>, Object>(a, a.getValue(entity))).collect(Collectors.toList());
    }

    protected Object getDbSupportedAttributeValue(Object v) {
        Object valueForDb = null;
        // TODO RBe: Conversion of another data types when not supported their passing to Mongo DB?
        if (v instanceof Instant) {
            valueForDb = MongoConversions.storeDate((Instant)v);
        } else if (v instanceof BigDecimal) {
            valueForDb = MongoConversions.storeDecimal((BigDecimal)v);
        } else if (v instanceof Enum) {
            valueForDb = MongoConversions.storeEnum((Enum)v);
        } else {
            valueForDb = v;
        }
        return valueForDb;
    }

    /**
     * Algorithm for creating new document id's. Just generates
     * and returns new one.
     * @return new document id
     */
    protected String generateId() {
        return ObjectId.get().toHexString();
    }

    protected String getIdFieldName() {
        return FLD_ID;
    }

    protected Document findOneAndUpdate(List<Pair<Attribute<T, ?>, Object>> primaryAttributesWithValues, List<Pair<Attribute<T, ?>, Object>> updatedAttributesWithValues) {
        Document doc = updateDocWithAttributes(new Document(), updatedAttributesWithValues);
        doc.remove(getIdFieldName()); // sanity check that _id won't be updated
        return getCollection().findOneAndUpdate( // atomic find and modify
            updateDocWithAttributes(new Document(), primaryAttributesWithValues),
            new Document().append("$set", doc),
            new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    protected MongoFilter conditionsToMongoFilter(List<Condition> conditions) {
        final MongoFilter mf = new MongoFilter();
        if (conditions != null) {
            List<MongoCondition> mongoConditions = conditions.stream().map(c -> getConditionBuilder().build(c, this::getDbSupportedAttributeValue)).collect(Collectors.toList());
            mongoConditions.forEach(c -> mf.add(c.getCondition()));
        }
        return mf;
    }

    // TODO RBe: Read/write concerns configuration
}
