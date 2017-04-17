/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import cz.etn.overview.Filter;
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.repo.FilterCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps data entity to database attributes.
 * This is interface, not an abstract class, so the implementation can be an enum or some regular class.
 * @param <T> type of entity
 * @param <F> type of filter for filtering according to entity attributes
 * @author Radek Beran
 */
public interface EntityMapper<T, F extends Filter> {

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
	List<FilterCondition> composeFilterConditions(F filter);
	
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
}
