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
	
	Object getAttributeValue(T entity);
		
	T entityUpdatedWithAttributeValue(T entity, AttributeSource attributeSource, String alias);
	
	String getAttributeName();
	
	default public String getAttributeName(String alias) {
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
	default public boolean isPrimaryAttribute() {
		return false;
	}
}
