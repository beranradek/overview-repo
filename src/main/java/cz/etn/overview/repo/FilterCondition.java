/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.funs.CollectionFuns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Condition for SQL WHERE clause.
 * @author Radek Beran
 */
public final class FilterCondition {

	public static final String CONTAINS_WITH_PLACEHOLDER = "LIKE CONCAT('%', ?, '%')";
	public static final List<Object> EMPTY_VALUES = Collections.unmodifiableList(new ArrayList<>());
	private final String conditionWithPlaceholders;
	private final List<Object> values;

	/**
	 * Equals condition for one attribute with one value.
	 * @param attrName
	 * @param value
	 * @return
	 */
	public static FilterCondition eq(String attrName, Object value) {
		List<Object> values = new ArrayList<>();
		values.add(value);
		return new FilterCondition(attrName + "=?", values);
	}

	/**
	 * Condition on NULL value of attribute.
	 * @param attrName
	 * @return
	 */
	public static FilterCondition nullValue(String attrName) {
		return new FilterCondition(attrName + " IS NULL", new ArrayList<>());
	}

	/**
	 * Attribute value contains some given value.
	 * @param attrName
	 * @param value
	 * @return
	 */
	public static FilterCondition contains(String attrName, Object value) {
		List<Object> values = new ArrayList<>();
		values.add(value);
		return new FilterCondition(attrName + " " + CONTAINS_WITH_PLACEHOLDER, values);
	}

	/**
	 * Attribute value is contained in given values.
	 * @param attrName
	 * @param values
	 * @return
	 */
	public static FilterCondition in(String attrName, List<Object> values) {
		FilterCondition condition = null;
		if (values != null && !values.isEmpty()) {
			String[] placeholders = new String[values.size()];
			Arrays.fill(placeholders, "?");
			condition = new FilterCondition(attrName + " IN (" + CollectionFuns.join(Arrays.asList(placeholders), ", ") + ")", values);
		} else {
			// empty values for IN, value of attribute is certainly not among empty values
			condition = new FilterCondition("1=0", EMPTY_VALUES);
		}
		return condition;
	}
	
	public FilterCondition(String conditionWithPlaceholders, List<Object> values) {
		this.conditionWithPlaceholders = conditionWithPlaceholders;
		this.values = values;
	}
	
	public String getConditionWithPlaceholders() {
		return conditionWithPlaceholders;
	}
	
	public List<Object> getValues() {
		return values;
	}
}
