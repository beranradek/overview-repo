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
package cz.etn.overview.mapper;

import cz.etn.overview.common.Pair;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.repo.Conditions;
import cz.etn.overview.sql.mapper.JoinEntityMapperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps data entity to database attributes.
 * This is interface, not an abstract class, so the implementation can be an enum or some regular class.
 * @param <T> type of entity
 * @param <F> type of filter for filtering according to entity attributes
 * @author Radek Beran
 */
public interface EntityMapper<T, F> {

	/**
	 * Name of database table/collection that contains entities.
	 * @return
	 */
	String getDataSet();

	/**
	 * Creates new instance of entity.
	 * @return
     */
	T createEntity();

	/**
	 * Returns definition of entity attributes.
	 * @return
	 */
	List<Attribute<T, ?>> getAttributes();

	/**
	 * <p>Compose conditions from given filter.
	 * <p>NOTE: This method is part of entity mapper because of filtering is tightly bound with available entity attributes
	 * and filter composition with attributes composition.
	 * @param filter
	 * @return
	 */
	List<Condition> composeFilterConditions(F filter);

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param id
	 * @return
	 */
	default <K> List<Condition> composeFilterConditionsForPrimaryKey(K id) {
		List<Condition> conditions = new ArrayList<>();
		List<Attribute<T, ?>> pkAttributes = getPrimaryAttributes();
		if (pkAttributes.size() == 1) {
			conditions.add(Conditions.eq((Attribute<T, K>)pkAttributes.get(0), id));
		} else {
			// composed primary key
			conditions.addAll(composeFilterConditionsForCompositePrimaryKey(id));
		}
		return conditions;
	}

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param entity
	 * @return
	 */
	default <K> List<Condition> composeFilterConditionsForPrimaryKeyOfEntity(T entity) {
		List<Condition> conditions = new ArrayList<>();
		for (Attribute<T, ?> attr : getPrimaryAttributes()) {
			conditions.add(Conditions.eq((Attribute<T, Object>)attr, attr.getValue(entity)));
		}
		return conditions;
	}

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param key
	 * @return
	 */
	default <K> List<Condition> composeFilterConditionsForCompositePrimaryKey(K key) {
		List<Condition> conditions = new ArrayList<>();
		List<Pair<Attribute<T, ?>, Object>> attributesToValues = decomposePrimaryKey(key);
		if (attributesToValues != null) {
			for (Pair<Attribute<T, ?>, Object> p : attributesToValues) {
				Attribute<T, ?> attr = p.getFirst();
				Object value = p.getSecond();
				conditions.add(Conditions.eq((Attribute<T, Object>)attr, value));
			}
		}
		return conditions;
	}

	default <K> List<Pair<Attribute<T, ?>, Object>> decomposePrimaryKey(K key) {
		List<Pair<Attribute<T, ?>, Object>> attributesToValues = new ArrayList<>();
		List<Attribute<T, ?>> pkAttributes = getPrimaryAttributes();
		if (pkAttributes.isEmpty()) {
			throw new IllegalStateException("Please define some primary attributes of entity (data set " + getDataSet() + ")");
		} else if (pkAttributes.size() == 1) {
			attributesToValues.add(new Pair<>(pkAttributes.get(0), key));
		} else {
			// composed primary key
			throw new UnsupportedOperationException("Decomposition of composite key to pairs: attribute to value is not implemented. Key: " + key);
		}
		return attributesToValues;
	}

	default <K> List<Pair<Attribute<T, ?>, Object>> decomposePrimaryKeyOfEntity(T entity) {
		List<Pair<Attribute<T, ?>, Object>> attributesToValues = new ArrayList<>();
		List<Attribute<T, ?>> pkAttributes = getPrimaryAttributes();
		if (pkAttributes.isEmpty()) {
			throw new IllegalStateException("Please define some primary attributes of entity (data set " + getDataSet() + ")");
		} else {
			List<Object> pkValues = getPrimaryAttributeValues(entity);
			for (int i = 0; i < pkAttributes.size(); i++) {
				attributesToValues.add(new Pair<>(pkAttributes.get(i), pkValues.get(i)));
			}
		}
		return attributesToValues;
	}
	
	/**
	 * Returns string that can be used to prefix all database attribute names for mapped entity to gain unique aliases.
	 * @return
	 */
	default String getAliasPrefix() {
		return getDataSet() + "_";
	}

	default List<Attribute<T, ?>> getPrimaryAttributes() {
		List<Attribute<T, ?>> primAttrs = new ArrayList<>();
		List<Attribute<T, ?>> attributes = getAttributes();
		if (attributes != null) {
			for (Attribute<T, ?> attr : attributes) {
				if (attr.isPrimary()) {
					primAttrs.add(attr);
				}
			}
		}
		return primAttrs;
	}
	
	/**
	 * Returns names of database attributes that represent primary key.
	 * @return
	 */
	default List<String> getPrimaryAttributeNames() {
		return getPrimaryAttributes().stream().map(attr -> attr.getName()).collect(Collectors.toList());
	}

	/**
	 * Extracts values of primary key attributes.
	 * @param entity
	 * @return
	 */
	default List<Object> getPrimaryAttributeValues(T entity) {
		return getPrimaryAttributes().stream().map(attr -> attr.getValue(entity)).collect(Collectors.toList());
	}
	
	/**
	 * Returns names of database attributes.
	 * @return
	 */
	default List<String> getAttributeNames() {
		return getAttributes().stream().map(v -> v.getName()).collect(Collectors.toList());
	}
	
	/**
	 * Extracts values for database attributes from given entity instance.
	 * @param instance
	 * @return
	 */
	default List<Object> getAttributeValues(T instance) {
		List<Object> attrValues = new ArrayList<>();
		for (Attribute<T, ?> fld : getAttributes()) {
			attrValues.add(fld.getValue(instance));
		}
		return attrValues;
	}

	/**
	 * Returns full attribute names with aliases that can be used to extract attribute values from {@link AttributeSource}.
	 * @return
     */
	default List<String> getAttributeNamesFullAliased() {
		String aliasPrefix = getAliasPrefix();
		return getAttributes().stream()
			.map(v -> (v.getNameFull() + (aliasPrefix != null ? (" AS " + aliasPrefix + v.getName()) : "")))
			.collect(Collectors.toList());
	}
	
	/**
	 * Builds new data entity from attribute source.
	 * @param attributeSource
	 * @param aliasPrefix alias prefix for all database fields 
	 * @return
	 */
	default T buildEntity(AttributeSource attributeSource, String aliasPrefix) {
		T instance = createEntity();
		for (Attribute<T, ?> attr : getAttributes()) {
			String alias = null;
			if (aliasPrefix != null) {
				alias = aliasPrefix + attr.getName();
			}
			instance = attr.entityWithAttribute(instance, attributeSource, attr.getName(alias));
		}
		boolean primaryKeyFilled = getPrimaryAttributeValues(instance).stream().anyMatch(v -> v != null);
		return primaryKeyFilled ? instance : null;
	}
	
	/**
	 * Builds new data entity from attribute source.
	 * @param attributeSource
	 * @return
	 */
	default T buildEntity(AttributeSource attributeSource) {
		return buildEntity(attributeSource, null);
	}

	/**
	 * Creates entity mapper for joined entities.
	 * @param secondMapper mapper of second entity to join with
	 * @param joinType type of join operation
	 * @param <U> type of second entity to join with
	 * @param <G> type of second entity filter
	 * @param <V> type of resulting entity representing joined records, this can be also the same type as T or U
	 * @param <H> type of resulting entity filter
	 * @param <O> type of attribute used for join operation
	 * @return builder of join mapper for fetching joined entities
	 */
	default <U, G, V, H, O> JoinEntityMapperBuilder<T, F, U, G, V, H, O> join(EntityMapper<U, G> secondMapper, JoinType joinType, Class<V> resultingEntityClass, Class<H> resultingFilterClass, Class<O> joinAttrClass) {
		return new JoinEntityMapperBuilder<>(this, secondMapper, joinType);
	}

	default <U, G, V, H, O> JoinEntityMapperBuilder<T, F, U, G, V, H, O> join(EntityMapper<U, G> secondMapper, Class<V> resultingEntityClass, Class<H> resultingFilterClass, Class<O> joinAttrClass) {
		return new JoinEntityMapperBuilder<>(this, secondMapper, JoinType.INNER);
	}

	default <U, G, V, H, O> JoinEntityMapperBuilder<T, F, U, G, V, H, O> leftJoin(EntityMapper<U, G> secondMapper, Class<V> resultingEntityClass, Class<H> resultingFilterClass, Class<O> joinAttrClass) {
		return new JoinEntityMapperBuilder<>(this, secondMapper, JoinType.LEFT);
	}

	default <U, G, V, H, O> JoinEntityMapperBuilder<T, F, U, G, V, H, O> rightJoin(EntityMapper<U, G> secondMapper, Class<V> resultingEntityClass, Class<H> resultingFilterClass, Class<O> joinAttrClass) {
		return new JoinEntityMapperBuilder<>(this, secondMapper, JoinType.RIGHT);
	}
}
