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
package org.xbery.overview.domain;

/**
 * Address.
 * @author Radek Beran
 */
public class Address {

	private String street;

	private String streetNumber;

	private String city;

	private String postalCode;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (street != null) {
			sb.append(street);
		}
		if (street != null && streetNumber != null) {
			sb.append(" ");
		}
		if (streetNumber != null) {
			sb.append(streetNumber);
		}
		if (sb.length() > 0 && (postalCode != null || city != null)) {
			sb.append(", ");
		}
		if (postalCode != null) {
			sb.append(postalCode);
		}
		if (postalCode != null && city != null) {
			sb.append(" ");
		}
		if (city != null) {
			sb.append(city);
		}
		return sb.toString();
	}

}
