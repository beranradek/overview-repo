/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview;

import cz.etn.overview.domain.Voucher;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Voucher data for use in tests.
 * @author Radek Beran
 */
public class VoucherTestData {

	public Voucher newVoucher(String code) {
		Voucher voucher = new Voucher();
		voucher.setCode(code);
		voucher.setCreationTime(Instant.now());
		voucher.setDiscountPrice(BigDecimal.valueOf(100000, 2)); // database stores decimal with two decimal digits
		voucher.setInvoiceNote("January invoice");
		return voucher;
	}
}
