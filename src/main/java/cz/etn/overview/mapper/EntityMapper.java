/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import cz.etn.overview.common.Pair;
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.repo.Condition;
import cz.etn.overview.repo.join.JoinType;

import java.util.ArrayList;
import java.util.Collections;
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

	T createEntity();

	/**
	 * Name of database table/collection that contains entities.
	 * @return
     */
	String getDataSet();
	
	/**
	 * Returns string that can be used to prefix all database attribute names for mapped entity to gain unique aliases.
	 * @return
	 */
	default String getAliasPrefix() {
		return getDataSet() + "_";
	}
	
	/**
	 * Returns names of database attributes that represents primary key.
	 * @return
	 */
	List<String> getPrimaryAttributeNames();

	/**
	 * Extracts values of primary key attributes.
	 * @param instance
	 * @return
	 */
	List<Object> getPrimaryAttributeValues(T instance);
	
	/**
	 * Returns names of database attributes.
	 * @return
	 */
	List<String> getAttributeNames();
	
	/**
	 * Extracts values for database attributes from given entity instance.
	 * @param instance
	 * @return
	 */
	List<Object> getAttributeValues(T instance);

	/**
	 * Returns full attribute names with aliases that can be used to extract attribute values from {@link AttributeSource}.
	 * @return
     */
	List<String> getAttributeNamesFullAliased();
	
	/**
	 * Builds new data entity from attribute source.
	 * @param attributeSource
	 * @param aliasPrefix alias prefix for all database fields 
	 * @return
	 */
	T buildEntity(AttributeSource attributeSource, String aliasPrefix);

	/**
	 * <p>Compose conditions from given filter.
	 * <p>NOTE: This method is part of entity mapper because of filtering is tightly bound with available entity attributes
	 * and filter composition with attributes composition.
	 * @param filter
	 * @return
	 */
	List<Condition> composeFilterConditions(F filter);
	
	/**
	 * Builds new data entity from attribute source.
	 * @param attributeSource
	 * @return
	 */
	default T buildEntity(AttributeSource attributeSource) {
		return buildEntity(attributeSource, null);
	}
	
	default String getAttributeNamesCommaSeparated() {
		return CollectionFuns.join(getAttributeNames(), ",");
	}
	
	/** Returns string with comma-separated question marks, one for each database column name. */
	default String getPlaceholdersCommaSeparated() {
		List<String> questionMarks = new ArrayList<>(Collections.nCopies(getAttributeNames().size(), "?"));
		return CollectionFuns.join(questionMarks, ",");
	}
	
	/** Returns string with comma-separated database attribute names with placeholder values in form suitable for SQL update: column1=?,column2=?,... */
	default String getAttributeNamesEqToPlaceholdersCommaSeparated() {
		return CollectionFuns.join(getAttributeNames().stream().map(attrName -> attrName + "=?").collect(Collectors.toList()), ",");
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

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param id
	 * @return
	 */
	default <K> List<Condition> composeFilterConditionsForPrimaryKey(K id) {
		List<Condition> conditions = new ArrayList<>();
		List<String> names = getPrimaryAttributeNames();
		if (names.size() == 1) {
			conditions.add(Condition.eq(names.get(0), id));
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
	default <K> List<Condition> composePrimaryKeyFilterConditions(T entity) {
		List<Condition> conditions = new ArrayList<>();
		List<String> names = getPrimaryAttributeNames();
		List<Object> values = getPrimaryAttributeValues(entity);
		for (int i = 0; i < names.size(); i++) {
			String attrName = names.get(i);
			Object attrValue = values.get(i);
			conditions.add(Condition.eq(attrName, attrValue));
		}
		return conditions;
	}

	/**
	 * Composes filter conditions to match primary key attributes.
	 * @param id
	 * @return
	 */
	default <K> List<Condition> composeFilterConditionsForCompositePrimaryKey(K id) {
		throw new UnsupportedOperationException("Decomposition of composite key to filter conditions is not implemented. Key: " + id);
	}
}
