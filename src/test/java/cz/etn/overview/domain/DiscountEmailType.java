/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

/**
 * Type of discount e-mail sent.
 * @author Radek Beran
 */
public enum DiscountEmailType {
	/** Customer has not achieved any discount. */
	NO_DISCOUNT("voucherNoDiscount"),
	
	ONE_SUPPLY_POINT_DISCOUNT("voucherDiscount"),
	
	MORE_SUPPLY_POINTS_DISCOUNT("voucherDiscount");
	
	private final String notificationCode;
	
	private DiscountEmailType(String notificationCode) {
		this.notificationCode = notificationCode;
	}
	
	public String getNotificationCode() {
		return notificationCode;
	}
}
