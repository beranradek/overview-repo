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
