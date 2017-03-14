/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

/**
 * Mapped attribute.
 * @author Radek Beran
 */
public interface AttributeMapping<T> {
	/** Separator between prefix and attribute name. */
	public static final String ATTR_NAME_SEPARATOR = ".";

	/**
	 * Value of attribute extracted from given entity.
	 * @param entity
	 * @return
     */
	Object getAttributeValue(T entity);

	/**
	 * Name of attribute.
	 * @return
     */
	String getAttributeName();

	/**
	 * Returns instance of entity updated with given attribute.
	 * @param entity
	 * @param attributeSource
	 * @param attributeName name of attribute that should be used to extract attribute value from given attribute source
     * @return
     */
	T entityWithAttribute(T entity, AttributeSource attributeSource, String attributeName);
	
	default String getAttributeName(String alias) {
		String attrName = null;
		if (alias == null || alias.isEmpty()) {
			attrName = getAttributeName();
		} else {
			attrName = alias;
		}
		return attrName;
	}
	
	/**
	 * Whether this database attribute is part of primary key.
	 * @return
	 */
	default boolean isPrimaryAttribute() {
		return false;
	}
}
