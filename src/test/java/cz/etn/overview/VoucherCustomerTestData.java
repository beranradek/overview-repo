/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview;

import cz.etn.overview.domain.DiscountEmailType;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.domain.VoucherCustomer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Voucher customer data for use in tests.
 * @author Radek Beran
 */
public class VoucherCustomerTestData {

	public VoucherCustomer newVoucherCustomer(String email, String firstName, String lastName) {
		VoucherCustomer voucherCustomer = new VoucherCustomer();
		voucherCustomer.setCreationTime(Instant.now());
		voucherCustomer.setEmail(email);
		voucherCustomer.setFirstName(firstName);
		voucherCustomer.setLastName(lastName);
		List<SupplyPoint> supplyPoints = new ArrayList<>();
		supplyPoints.add(createSupplyPoint("4100272309"));
		supplyPoints.add(createSupplyPoint("4100272310"));
		voucherCustomer.setSupplyPoints(supplyPoints);
		voucherCustomer.setDiscountEmailType(DiscountEmailType.MORE_SUPPLY_POINTS_DISCOUNT);
		voucherCustomer.setImportFileName("body_Benefit_body_rijen_31102016.xlsx");
		return voucherCustomer;
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
