/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.mapper;

/**
 * Mapped attribute interface.
 * @author Radek Beran
 */
public interface Attribute<E, A> {

	/**
	 * Value of attribute extracted from given entity.
	 * @param entity
	 * @return
     */
	A getValue(E entity);

	/**
	 * Name of attribute.
	 * @return
     */
	String getName();

	/**
	 * Returns instance of entity updated with given attribute.
	 * @param entity
	 * @param attributeSource
	 * @param attributeName name of attribute that should be used to extract attribute value from given attribute source
     * @return
     */
	E entityWithAttribute(E entity, AttributeSource attributeSource, String attributeName);
	
	default String getName(String alias) {
		String attrName = null;
		if (alias == null || alias.isEmpty()) {
			attrName = getName();
		} else {
			attrName = alias;
		}
		return attrName;
	}
	
	/**
	 * Whether this database attribute is part of primary key.
	 * @return
	 */
	default boolean isPrimary() {
		return false;
	}
}
