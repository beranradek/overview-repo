/*
 * Created on 13. 3. 2017 Copyright (c) 2017 Etnetera, a.s. All rights reserved. Intended for
 * internal use only. http://www.etnetera.cz
 */

package cz.etn.overview.domain;

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
