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
