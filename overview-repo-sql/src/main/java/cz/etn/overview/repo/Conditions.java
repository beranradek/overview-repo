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
package cz.etn.overview.repo;

import cz.etn.overview.filter.*;
import cz.etn.overview.mapper.Attribute;

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
	public static Condition eq(Attribute<?, ?> attribute, Object value) {
		return new EqCondition(attribute, value);
	}

	/**
	 * Equals condition for two attributes.
	 * @param attribute1
	 * @param attribute2
	 * @return
	 */
	public static Condition eqAttributes(Attribute<?, ?> attribute1, Attribute<?, ?> attribute2) {
		return new EqAttributesCondition(attribute1, attribute2);
	}

	/**
	 * Attribute value contains some given value.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static Condition contains(Attribute<?, ?> attribute, Object value) {
		return new ContainsCondition(attribute, value);
	}

	/**
	 * Attribute value is contained in given values.
	 * @param attribute
	 * @param values
	 * @return
	 */
	public static Condition in(Attribute<?, ?> attribute, List<Object> values) {
		return new InCondition(attribute, values);
	}

	// TODO RBe: Conditions for ne, lte, gte, lt, gt

}
