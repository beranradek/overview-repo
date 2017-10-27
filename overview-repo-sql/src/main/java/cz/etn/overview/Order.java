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
package cz.etn.overview;

import cz.etn.overview.mapper.Attribute;

import java.io.Serializable;

/**
 * Ordering according to one entity attribute. Immutable class.
 * @author Radek Beran
 */
public final class Order implements Serializable {
	private static final long serialVersionUID = -8270735262783017159L;

	// Change this to typed attribute object? Maybe not, Order is handy as Serializable and attributes are not serializable.
	private final String attribute;

	private final boolean desc;

	public Order(Attribute<?, ?> attribute, boolean desc) {
		this.attribute = attribute.getNameFull();
		this.desc = desc;
	}

	public Order(Attribute<?, ?> attribute) {
		this(attribute, false);
	}

	/**
	 * Name or full (qualified) name of attribute.
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * True if sorted in descending order.
	 */
	public boolean isDesc() {
		return desc;
	}
	
	public String getDbString() {
		return attribute + (desc ? " DESC" : "");
	}

	@Override
	public String toString() {
		return "Order [attribute=" + attribute + ", desc=" + desc + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + (desc ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Order other = (Order)obj;
		if (attribute == null) {
			if (other.attribute != null) return false;
		} else if (!attribute.equals(other.attribute)) return false;
		if (desc != other.desc) return false;
		return true;
	}

}
