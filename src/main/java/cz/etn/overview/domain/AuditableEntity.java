/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

import java.time.Instant;

/**
 * Basic data entity attributes.
 * @author Radek Beran
 */
public abstract class AuditableEntity {

	public static final String FLD_CREATION_TIME = "creation_time";
	
	/**
	 * Time when the entity was created.
	 */
	private Instant creationTime;
	
	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}
	
	/**
	 * Time when the entity was created.
	 */
	public Instant getCreationTime() {
		return creationTime;
	}
}
