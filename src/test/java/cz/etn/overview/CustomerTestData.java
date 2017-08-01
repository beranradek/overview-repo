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

import cz.etn.overview.domain.DiscountEmailType;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.domain.Customer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Voucher customer data for use in tests.
 * @author Radek Beran
 */
public class CustomerTestData {

	public Customer newVoucherCustomer(String email, String firstName, String lastName) {
		Customer customer = new Customer();
		customer.setCreationTime(Instant.now());
		customer.setEmail(email);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		List<SupplyPoint> supplyPoints = new ArrayList<>();
		supplyPoints.add(createSupplyPoint("4100272309"));
		supplyPoints.add(createSupplyPoint("4100272310"));
		customer.setSupplyPoints(supplyPoints);
		customer.setDiscountEmailType(DiscountEmailType.MORE_SUPPLY_POINTS_DISCOUNT);
		customer.setImportFileName("body_Benefit_body_rijen_31102016.xlsx");
		return customer;
	}
	
	public SupplyPoint createSupplyPoint(String code) {
		SupplyPoint sp = new SupplyPoint();
		sp.setCode(code);
		return sp;
	}

	public Voucher createVoucher(String code, String reservedBy) {
		Voucher voucher = new Voucher();
		voucher.setCode(code);
		Instant creationTime = Instant.now();
		voucher.setCreationTime(creationTime);
		voucher.setValidFrom(creationTime);
		voucher.setValidFrom(creationTime.plus(Duration.ofDays(90)));
		voucher.setReservedBy(reservedBy);
		return voucher;
	}
}
