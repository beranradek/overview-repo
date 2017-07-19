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

import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.mapper.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Condition for SQL WHERE clause or JOIN ON clause.
 * @author Radek Beran
 */
public final class Condition {

	public static final String CONTAINS_WITH_PLACEHOLDER = "LIKE CONCAT('%', ?, '%')";
	public static final List<Object> EMPTY_VALUES = Collections.unmodifiableList(new ArrayList<>());
	private final String conditionWithPlaceholders;
	private final List<Object> values;

	/**
	 * Equals condition for one attribute with one value.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static Condition eq(Attribute<?, ?> attribute, Object value) {
		List<Object> values = new ArrayList<>();
		values.add(value);
		return new Condition(attribute.getNameFull() + "=?", values);
	}

	/**
	 * Equals condition for two attributes.
	 * @param attribute1
	 * @param attribute2
	 * @return
	 */
	public static Condition eqAttributes(Attribute<?, ?> attribute1, Attribute<?, ?> attribute2) {
		return new Condition(attribute1.getNameFull() + "=" + attribute2.getNameFull(), EMPTY_VALUES);
	}

	/**
	 * Condition on NULL value of attribute.
	 * @param attribute
	 * @return
	 */
	public static Condition nullValue(Attribute<?, ?> attribute) {
		return new Condition(attribute.getNameFull() + " IS NULL", new ArrayList<>());
	}

	/**
	 * Attribute value contains some given value.
	 * @param attribute
	 * @param value
	 * @return
	 */
	public static Condition contains(Attribute<?, ?> attribute, Object value) {
		List<Object> values = new ArrayList<>();
		values.add(value);
		return new Condition(attribute.getNameFull() + " " + CONTAINS_WITH_PLACEHOLDER, values);
	}

	/**
	 * Attribute value is contained in given values.
	 * @param attribute
	 * @param values
	 * @return
	 */
	public static Condition in(Attribute<?, ?> attribute, List<Object> values) {
		Condition condition = null;
		if (values != null && !values.isEmpty()) {
			String[] placeholders = new String[values.size()];
			Arrays.fill(placeholders, "?");
			condition = new Condition(attribute.getNameFull() + " IN (" + CollectionFuns.join(Arrays.asList(placeholders), ", ") + ")", values);
		} else {
			// empty values for IN, value of attribute is certainly not among empty values
			condition = new Condition("1=0", EMPTY_VALUES);
		}
		return condition;
	}
	
	public Condition(String conditionWithPlaceholders, List<Object> values) {
		this.conditionWithPlaceholders = conditionWithPlaceholders;
		this.values = values;
	}

	public Condition(String conditionWithPlaceholders) {
		this(conditionWithPlaceholders, new ArrayList<>());
	}
	
	public String getConditionWithPlaceholders() {
		return conditionWithPlaceholders;
	}
	
	public List<Object> getValues() {
		return values;
	}
}
