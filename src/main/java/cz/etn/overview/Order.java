/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package cz.etn.overview;

import java.io.Serializable;

/**
 * Ordering according to one entity attribute. Immutable class.
 * @author Radek Beran
 */
public final class Order implements Serializable {
	private static final long serialVersionUID = -8270735262783017159L;

	private final String attribute;

	private final boolean desc;

	public Order(String attribute, boolean desc) {
		this.attribute = attribute;
		this.desc = desc;
	}

	public String getAttribute() {
		return attribute;
	}

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
