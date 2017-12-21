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
package org.xbery.overview.data;

import org.xbery.overview.domain.Customer;
import org.xbery.overview.domain.DiscountEmailType;
import org.xbery.overview.domain.SupplyPoint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Voucher customer data for use in tests.
 * @author Radek Beran
 */
public class CustomerTestData {

	private final SupplyPointTestData supplyPointTestData = new SupplyPointTestData();

	public Customer createCustomerWithSupplyPoints(String email, String firstName, String lastName) {
		Customer customer = createCustomer(email, firstName, lastName);
		List<SupplyPoint> supplyPoints = new ArrayList<>();
		supplyPoints.add(supplyPointTestData.createSupplyPoint("4100272309"));
		supplyPoints.add(supplyPointTestData.createSupplyPoint("4100272310"));
		customer.setSupplyPoints(supplyPoints);
		return customer;
	}

	public Customer createCustomer(String email, String firstName, String lastName) {
		Customer customer = new Customer();
		customer.setCreationTime(Instant.now());
		customer.setEmail(email);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setDiscountEmailType(DiscountEmailType.MORE_SUPPLY_POINTS_DISCOUNT);
		customer.setImportFileName("body_Benefit_body_rijen_31102016.xlsx");
		return customer;
	}
}
