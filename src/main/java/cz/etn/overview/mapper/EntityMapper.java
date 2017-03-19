/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

import cz.etn.overview.funs.CollectionFuns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps data entity to database attributes.
 * This is interface, not an abstract class, so the implementation can be an enum.
 * @author Radek Beran
 */
public interface EntityMapper<T> {
	
	String getTableName();
	
	/**
	 * Returns string that can be used to prefix all database attribute names for mapped entity to gain unique aliases.
	 * @return
	 */
	default String getAliasPrefix() {
		return getTableName() + "_";
	}
	
	/**
	 * Returns name of database attribute that represents primary key.
	 * @return
	 */
	String getPrimaryAttributeName();
	
	/**
	 * Returns names of database attributes.
	 * @return
	 */
	List<String> getAttributeNames();
	
	/**
	 * Returns names of database attributes with given prefix.
	 * @param tableName
	 * @param aliasPrefix
	 * @return
	 */
	List<String> getAttributeNamesWithPrefix(String tableName, String aliasPrefix);
	
	/**
	 * Extracts values for database attributes from given entity instance.
	 * @param instance
	 * @return
	 */
	List<Object> getAttributeValues(T instance);
	
	/**
	 * Extracts value of primary key attribute.
	 * @param instance
	 * @return
	 */
	Object getPrimaryAttributeValue(T instance);
	
	/**
	 * Builds new data entity from attribute source.
	 * @param attributeSource
	 * @param aliasPrefix alias prefix for all database fields 
	 * @return
	 */
	T buildEntity(AttributeSource attributeSource, String aliasPrefix);
	
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
	
	default String getAttributeNamesCommaSeparatedWithPrefix(String tableName, String aliasPrefix) {
		return CollectionFuns.join(getAttributeNamesWithPrefix(tableName, aliasPrefix), ",");
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
