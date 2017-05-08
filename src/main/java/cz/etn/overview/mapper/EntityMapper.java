/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import cz.etn.overview.common.Pair;
import cz.etn.overview.repo.Condition;
import cz.etn.overview.repo.join.JoinType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
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
			conditions.add(Condition.eq(pkAttributes.get(0), id));
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
			conditions.add(Condition.eq(attr, attr.getValue(entity)));
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
				conditions.add(Condition.eq(attr, value));
			}
		}
		return conditions;
	}

	default <K> List<Pair<Attribute<T, ?>, Object>> decomposePrimaryKey(K key) {
		List<Pair<Attribute<T, ?>, Object>> attributesToValues = new ArrayList<>();
		List<Attribute<T, ?>> pkAttributes = getPrimaryAttributes();
		if (pkAttributes.size() == 1) {
			attributesToValues.add(new Pair<>(pkAttributes.get(0), key));
		} else {
			// composed primary key
			throw new UnsupportedOperationException("Decomposition of composite key to pairs: attribute to value is not implemented. Key: " + key);
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
	 * @param onConditions condition(s) for joining the tables
	 * @param composeEntity function combining joined entities together to resulting entity
	 * @param decomposeFilter function returning partial filters for first and second joined entity
	 * @param joinType type of join operation
	 * @param <U> type of second entity to join with
	 * @param <G> type of second entity filter
	 * @param <V> type of resulting entity representing joined records, this can be also the same type as T or U
	 * @param <H> type of resulting entity filter
	 * @return mapper for joined entities
	 */
	default <U, G, V, H> EntityMapper<V, H> join(EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter, JoinType joinType) {
		return new JoinEntityMapper(this, secondMapper, onConditions, composeEntity, decomposeFilter, joinType);
	}

	default <U, G, V, H> EntityMapper<V, H> join(EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		return join(secondMapper, onConditions, composeEntity, decomposeFilter, JoinType.INNER);
	}

	default <U, G, V, H> EntityMapper<V, H> join(EntityMapper<U, G> secondMapper, Condition onCondition, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		List<Condition> onConditions = new ArrayList<>();
		onConditions.add(onCondition);
		return join(secondMapper, onConditions, composeEntity, decomposeFilter);
	}

	default <U, G, V, H> EntityMapper<V, H> leftJoin(EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		return join(secondMapper, onConditions, composeEntity, decomposeFilter, JoinType.LEFT);
	}

	default <U, G, V, H> EntityMapper<V, H> leftJoin(EntityMapper<U, G> secondMapper, Condition onCondition, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		List<Condition> onConditions = new ArrayList<>();
		onConditions.add(onCondition);
		return leftJoin(secondMapper, onConditions, composeEntity, decomposeFilter);
	}

	default <U, G, V, H> EntityMapper<V, H> rightJoin(EntityMapper<U, G> secondMapper, List<Condition> onConditions, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		return join(secondMapper, onConditions, composeEntity, decomposeFilter, JoinType.RIGHT);
	}

	default <U, G, V, H> EntityMapper<V, H> rightJoin(EntityMapper<U, G> secondMapper, Condition onCondition, BiFunction<T, U, V> composeEntity, Function<H, Pair<F, G>> decomposeFilter) {
		List<Condition> onConditions = new ArrayList<>();
		onConditions.add(onCondition);
		return rightJoin(secondMapper, onConditions, composeEntity, decomposeFilter);
	}
}
