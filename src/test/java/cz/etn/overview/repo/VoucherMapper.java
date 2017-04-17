/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import cz.etn.overview.Filter;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.Attr;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.DynamicEntityMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of voucher attributes to database attributes.
 * @author Radek Beran
 */
public class VoucherMapper extends DynamicEntityMapper<Voucher, Filter> {

	/** Mapped entity class. */
	private static final Class<Voucher> cls = Voucher.class;
	private static final String DB_TABLE_NAME = "voucher";
	private static final VoucherMapper INSTANCE = new VoucherMapper();

	public final Attribute<Voucher, String> code;
	public final Attribute<Voucher, Instant> creation_time;
	public final Attribute<Voucher, BigDecimal> discount_price;
	public final Attribute<Voucher, Instant> valid_from;
	public final Attribute<Voucher, Instant> valid_to;
	public final Attribute<Voucher, Instant> redemption_time;
	public final Attribute<Voucher, Instant> invalidation_time;
	public final Attribute<Voucher, String> invalidation_note;
	public final Attribute<Voucher, String> renewal_note;
	public final Attribute<Voucher, String> reserved_by;
	public final Attribute<Voucher, String> redeemed_by;
	public final Attribute<Voucher, String> sold_by;
	public final Attribute<Voucher, Instant> invoice_time;
	public final Attribute<Voucher, String> invoice_note;

	private VoucherMapper() {
		code = add(Attr.ofString(cls, "code").primary().get(e -> e.getCode()).set((e, a) -> e.setCode(a)));
		creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
		discount_price = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).set((e, a) -> e.setDiscountPrice(a)));
		valid_from = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()).set((e, a) -> e.setValidFrom(a)));
		valid_to = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()).set((e, a) -> e.setValidTo(a)));
		redemption_time = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()).set((e, a) -> e.setRedemptionTime(a)));
		invalidation_time = add(Attr.ofInstant(cls, "invalidation_time").get(e -> e.getInvalidationTime()).set((e, a) -> e.setInvalidationTime(a)));
		invalidation_note = add(Attr.ofString(cls, "invalidation_note").get(e -> e.getInvalidationNote()).set((e, a) -> e.setInvalidationNote(a)));
		renewal_note = add(Attr.ofString(cls, "renewal_note").get(e -> e.getRenewalNote()).set((e, a) -> e.setRenewalNote(a)));
		reserved_by = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).set((e, a) -> e.setReservedBy(a)));
		redeemed_by = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).set((e, a) -> e.setRedeemedBy(a)));
		sold_by = add(Attr.ofString(cls, "sold_by").get(e -> e.getSoldBy()).set((e, a) -> e.setSoldBy(a)));
		invoice_time = add(Attr.ofInstant(cls, "invoice_time").get(e -> e.getInvoiceTime()).set((e, a) -> e.setInvoiceTime(a)));
		invoice_note = add(Attr.ofString(cls, "invoice_note").get(e -> e.getInvoiceNote()).set((e, a) -> e.setInvoiceNote(a)));
	}

	public static VoucherMapper getInstance() {
		return INSTANCE;
	}

	@Override
	public String getDataSet() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public Voucher createEntity() {
		return new Voucher();
	}

	@Override
	public List<FilterCondition> composeFilterConditions(Filter filter) {
		return new ArrayList<>();
	}
}
