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
package org.xbery.overview.mapper;

import java.util.Optional;
import java.util.function.Function;

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
	 * Class of entity.
	 * @return
	 */
	Class<E> getEntityClass();

	/**
	 * Class of attribute.
	 * @return
	 */
	Class<A> getAttributeClass();

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
	 * <p>This should be overridden by subclasses if prefixes should be supported.
	 * @param namePrefix
	 * @return
	 */
	Attribute<E, A> withNamePrefix(String namePrefix);

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

	/**
	 * Length constraint on attribute value (if any is defined).
	 * @return
     */
	default Optional<Integer> getMaxLength() {
		return Optional.empty();
	}

	/**
	 * Converts attribute to attribute of another type.
	 * @param attrClass
	 * @param toNewType
	 * @param toOldType
	 * @param <T>
	 * @return
	 */
	<T> Attribute<E, T> as(Class<T> attrClass, Function<A, T> toNewType, Function<T, A> toOldType);
}
