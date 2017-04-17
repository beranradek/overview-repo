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
 * @param <E> type of entity
 * @param <A> type of attribute
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
	 * <p>Name prefix for attribute derived from an entity so that full attribute name including entity qualification
	 * can be constructed.
	 * <p>This should be overridden by subclasses if prefixes should be supported. Default implementation returns {@code null}.
	 * @return
	 */
	default String getNamePrefix() {
		return null; // no name prefix by default
	}

	/**
	 * <p>Returns copy of this attribute with given name prefix set.
	 * <p>This should be overridden by subclasses if prefixes should be supported. Default implementation returns {@code this} attribute.
	 * @param namePrefix
	 * @return
	 */
	default Attribute<E, A> withNamePrefix(String namePrefix) {
		return this;
	}

	/**
	 * Returns instance of entity updated with given attribute.
	 * @param entity
	 * @param attributeSource
	 * @param attributeName name of attribute that should be used to extract attribute value from given attribute source
     * @return
     */
	E entityWithAttribute(E entity, AttributeSource attributeSource, String attributeName);

	/**
	 * Full name of attribute including "entity namespace".
	 * @return
	 */
	default String getNameFull() {
		String fullName = null;
		String prefix = getNamePrefix();
		if (prefix != null) {
			fullName = prefix + "." + getName();
		} else {
			fullName = getName();
		}
		return fullName;
	}
	
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
