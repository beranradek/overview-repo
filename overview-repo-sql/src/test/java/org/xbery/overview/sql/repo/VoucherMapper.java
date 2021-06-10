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
package org.xbery.overview.sql.repo;


import org.xbery.overview.domain.Voucher;
import org.xbery.overview.mapper.Attr;
import org.xbery.overview.mapper.Attribute;
import org.xbery.overview.filter.Condition;
import org.xbery.overview.mapper.AttributeSource;
import org.xbery.overview.mapper.DynamicEntityMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of voucher attributes to database attributes.
 * @author Radek Beran
 */
public class VoucherMapper extends DynamicEntityMapper<Voucher, Object> {

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

	protected VoucherMapper() {
		code = add(Attr.ofString(cls, "code").primary().get(e -> e.getCode()).maxLength(20));
		creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()));
		discount_price = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).maxLength(10));
		valid_from = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()));
		valid_to = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()));
		redemption_time = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()));
		invalidation_time = add(Attr.ofInstant(cls, "invalidation_time").get(e -> e.getInvalidationTime()));
		invalidation_note = add(Attr.ofString(cls, "invalidation_note").get(e -> e.getInvalidationNote()).maxLength(200));
		renewal_note = add(Attr.ofString(cls, "renewal_note").get(e -> e.getRenewalNote()).maxLength(200));
		reserved_by = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).maxLength(40));
		redeemed_by = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).maxLength(40));
		sold_by = add(Attr.ofString(cls, "sold_by").get(e -> e.getSoldBy()).maxLength(40));
		invoice_time = add(Attr.ofInstant(cls, "invoice_time").get(e -> e.getInvoiceTime()));
		invoice_note = add(Attr.ofString(cls, "invoice_note").get(e -> e.getInvoiceNote()).maxLength(400));
	}

	public static VoucherMapper getInstance() {
		return INSTANCE;
	}

	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}

	@Override
	public Voucher createEntity(AttributeSource attributeSource, List<Attribute<Voucher, ?>> attributes, String aliasPrefix) {
		Voucher voucher = new Voucher();
		voucher.setCode(code.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setCreationTime(creation_time.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setDiscountPrice(discount_price.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setValidFrom(valid_from.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setValidTo(valid_to.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setRedemptionTime(redemption_time.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setInvalidationTime(invalidation_time.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setInvalidationNote(invalidation_note.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setRenewalNote(renewal_note.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setReservedBy(reserved_by.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setRedeemedBy(redeemed_by.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setSoldBy(sold_by.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setInvoiceTime(invoice_time.getValueFromSource(attributeSource, aliasPrefix));
		voucher.setInvoiceNote(invoice_note.getValueFromSource(attributeSource, aliasPrefix));
		return voucher;
	}

	@Override
	public List<Condition> composeFilterConditions(Object filter) {
		return new ArrayList<>();
	}
}
