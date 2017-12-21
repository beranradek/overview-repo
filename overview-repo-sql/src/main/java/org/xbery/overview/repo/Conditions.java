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
package org.xbery.overview.repo;

import org.xbery.overview.filter.*;
import org.xbery.overview.mapper.Attribute;

import java.util.List;

/**
 * Conditions on entity attribute(s).
 * @author Radek Beran
 */
public class Conditions {

	/**
	 * Equals condition for one attribute with one value. Value can be {@code null} to set condition on null value.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> EqCondition<T, A> eq(Attribute<T, A> attribute, A value) {
		return new EqCondition(attribute, value);
	}

	/**
	 * Equals condition for two attributes.
	 * @param attribute1
	 * @param attribute2
	 * @return
	 */
	public static <T, U, A, B> EqAttributesCondition<T, U, A, B> eqAttributes(Attribute<T, A> attribute1, Attribute<U, B> attribute2) {
		return new EqAttributesCondition(attribute1, attribute2);
	}

	/**
	 * Attribute value contains some given value.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> ContainsCondition<T, A> contains(Attribute<T, A> attribute, A value) {
		return new ContainsCondition(attribute, value);
	}

	/**
	 * Attribute value is contained in given values.
	 * @param attribute
	 * @param values
	 * @return
	 */
	public static <T, A> InCondition<T, A> in(Attribute<T, A> attribute, List<A> values) {
		return new InCondition(attribute, values);
	}

	/**
	 * Less than condition.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> LtCondition<T, A> lt(Attribute<T, A> attribute, A value) {
		return new LtCondition(attribute, value);
	}

	/**
	 * Less than or equal condition.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> LteCondition<T, A> lte(Attribute<T, A> attribute, A value) {
		return new LteCondition(attribute, value);
	}

	/**
	 * Greater than condition.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> GtCondition<T, A> gt(Attribute<T, A> attribute, A value) {
		return new GtCondition(attribute, value);
	}

	/**
	 * Greater than or equal condition.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static <T, A> GteCondition<T, A> gte(Attribute<T, A> attribute, A value) {
		return new GteCondition(attribute, value);
	}

	/**
	 * OR condition.
	 * @param first
	 * @param second
	 * @return
	 */
	public static OrCondition or(Condition first, Condition second) {
		return new OrCondition(first, second);
	}

	/**
	 * AND condition.
	 * @param first
	 * @param second
	 * @return
	 */
	public static AndCondition and(Condition first, Condition second) {
		return new AndCondition(first, second);
	}

	// TODO RBe: Conditions for ne/negation

}
