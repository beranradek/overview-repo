/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.Attr;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.DynamicEntityMapper;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Mapping of voucher attributes to database attributes.
 * @author Radek Beran
 */
public class VoucherMapper extends DynamicEntityMapper<Voucher> {

	/** Mapped entity class. */
	private static final Class<Voucher> cls = Voucher.class;
	private static final String DB_TABLE_NAME = "voucher";
	public static final VoucherMapper INSTANCE = new VoucherMapper();

	public static final Attribute<Voucher, String> CODE = add(Attr.ofString(cls, "code").primary().get(e -> e.getCode()).set((e, a) -> e.setCode(a)));
	public static final Attribute<Voucher, Instant> CREATION_TIME = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
	public static final Attribute<Voucher, BigDecimal> DISCOUNT_PRICE = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).set((e, a) -> e.setDiscountPrice(a)));
	public static final Attribute<Voucher, Instant> VALID_FROM = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()).set((e, a) -> e.setValidFrom(a)));
	public static final Attribute<Voucher, Instant> VALID_TO = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()).set((e, a) -> e.setValidTo(a)));
	public static final Attribute<Voucher, Instant> REDEMPTION_TIME = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()).set((e, a) -> e.setRedemptionTime(a)));
	public static final Attribute<Voucher, Instant> INVALIDATION_TIME = add(Attr.ofInstant(cls, "invalidation_time").get(e -> e.getInvalidationTime()).set((e, a) -> e.setInvalidationTime(a)));
	public static final Attribute<Voucher, String> INVALIDATION_NOTE = add(Attr.ofString(cls, "invalidation_note").get(e -> e.getInvalidationNote()).set((e, a) -> e.setInvalidationNote(a)));
	public static final Attribute<Voucher, String> RENEWAL_NOTE = add(Attr.ofString(cls, "renewal_note").get(e -> e.getRenewalNote()).set((e, a) -> e.setRenewalNote(a)));
	public static final Attribute<Voucher, String> RESERVED_BY = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).set((e, a) -> e.setReservedBy(a)));
	public static final Attribute<Voucher, String> REDEEMED_BY = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).set((e, a) -> e.setRedeemedBy(a)));
	public static final Attribute<Voucher, String> SOLD_BY = add(Attr.ofString(cls, "sold_by").get(e -> e.getSoldBy()).set((e, a) -> e.setSoldBy(a)));
	public static final Attribute<Voucher, Instant> INVOICE_TIME = add(Attr.ofInstant(cls, "invoice_time").get(e -> e.getInvoiceTime()).set((e, a) -> e.setInvoiceTime(a)));
	public static final Attribute<Voucher, String> INVOICE_NOTE = add(Attr.ofString(cls, "invoice_note").get(e -> e.getInvoiceNote()).set((e, a) -> e.setInvoiceNote(a)));

	@Override
	public String getDataSet() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public Voucher createEntity() {
		return new Voucher();
	}
}
