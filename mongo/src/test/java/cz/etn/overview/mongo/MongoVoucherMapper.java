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
package cz.etn.overview.mongo;

import cz.etn.overview.domain.Voucher;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.mapper.Attr;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.DynamicEntityMapper;
import cz.etn.overview.mongo.repo.AbstractMongoRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of voucher attributes to database attributes.
 * @author Radek Beran
 */
public class MongoVoucherMapper extends DynamicEntityMapper<Voucher, Object> {

    /** Mapped entity class. */
    private static final Class<Voucher> cls = Voucher.class;
    private static final String COLLECTION_NAME = "voucher";
    private static final MongoVoucherMapper INSTANCE = new MongoVoucherMapper();

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

    private MongoVoucherMapper() {
        code = add(Attr.ofString(cls, AbstractMongoRepository.FLD_ID).primary().get(e -> e.getCode()).set((e, a) -> e.setCode(a)).maxLength(20));
        creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
        discount_price = add(Attr.ofBigDecimal(cls, "discount_price").get(e -> e.getDiscountPrice()).set((e, a) -> e.setDiscountPrice(a)).maxLength(10));
        valid_from = add(Attr.ofInstant(cls, "valid_from").get(e -> e.getValidFrom()).set((e, a) -> e.setValidFrom(a)));
        valid_to = add(Attr.ofInstant(cls, "valid_to").get(e -> e.getValidTo()).set((e, a) -> e.setValidTo(a)));
        redemption_time = add(Attr.ofInstant(cls, "redemption_time").get(e -> e.getRedemptionTime()).set((e, a) -> e.setRedemptionTime(a)));
        invalidation_time = add(Attr.ofInstant(cls, "invalidation_time").get(e -> e.getInvalidationTime()).set((e, a) -> e.setInvalidationTime(a)));
        invalidation_note = add(Attr.ofString(cls, "invalidation_note").get(e -> e.getInvalidationNote()).set((e, a) -> e.setInvalidationNote(a)).maxLength(200));
        renewal_note = add(Attr.ofString(cls, "renewal_note").get(e -> e.getRenewalNote()).set((e, a) -> e.setRenewalNote(a)).maxLength(200));
        reserved_by = add(Attr.ofString(cls, "reserved_by").get(e -> e.getReservedBy()).set((e, a) -> e.setReservedBy(a)).maxLength(40));
        redeemed_by = add(Attr.ofString(cls, "redeemed_by").get(e -> e.getRedeemedBy()).set((e, a) -> e.setRedeemedBy(a)).maxLength(40));
        sold_by = add(Attr.ofString(cls, "sold_by").get(e -> e.getSoldBy()).set((e, a) -> e.setSoldBy(a)).maxLength(40));
        invoice_time = add(Attr.ofInstant(cls, "invoice_time").get(e -> e.getInvoiceTime()).set((e, a) -> e.setInvoiceTime(a)));
        invoice_note = add(Attr.ofString(cls, "invoice_note").get(e -> e.getInvoiceNote()).set((e, a) -> e.setInvoiceNote(a)).maxLength(400));
    }

    public static MongoVoucherMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public String getDataSet() {
        return COLLECTION_NAME;
    }

    @Override
    public Voucher createEntity() {
        return new Voucher();
    }

    @Override
    public List<Condition> composeFilterConditions(Object filter) {
        return new ArrayList<>();
    }
}

