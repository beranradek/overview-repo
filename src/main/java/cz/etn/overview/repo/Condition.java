/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
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
