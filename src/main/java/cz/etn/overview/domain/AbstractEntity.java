/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

/**
 * Abstract data entity.
 * @author Radek Beran
 */
public abstract class AbstractEntity<T> extends AuditableEntity implements Identifiable<T> {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Identifiable)) return false;
		@SuppressWarnings("unchecked")
		Identifiable<T> other = (Identifiable<T>)obj;
		if (getId() == null) {
			if (other.getId() != null) return false;
		} else if (!getId().equals(other.getId())) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [id=" + getId() + "]";
	}
}
