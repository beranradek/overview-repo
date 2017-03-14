/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import java.util.List;

/**
 * Condition for SQL WHERE clause.
 * @author Radek Beran
 */
public final class FilterCondition {

	private final String conditionWithPlaceholders;
	private final List<Object> values;
	
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
