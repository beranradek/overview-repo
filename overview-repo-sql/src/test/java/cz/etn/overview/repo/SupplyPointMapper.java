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
package cz.etn.overview.repo;

import cz.etn.overview.domain.Address;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.mapper.Attr;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.DynamicEntityMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of supply point attributes to database fields.
 * @author Radek Beran
 */
public class SupplyPointMapper extends DynamicEntityMapper<SupplyPoint, SupplyPointFilter> {

	/** Mapped entity class. */
	private static final Class<SupplyPoint> cls = SupplyPoint.class;
	private static final String DB_TABLE_NAME = "voucher_supply_point";
	private static final SupplyPointMapper INSTANCE = new SupplyPointMapper();

	public final Attribute<SupplyPoint, Integer> id;
	public final Attribute<SupplyPoint, String> code;
	public final Attribute<SupplyPoint, Integer> customer_id;

	private SupplyPointMapper() {
		id = add(Attr.ofInteger(cls, "id").primary().get(e -> e.getId()).set((e, a) -> e.setId(a)));
		code = add(Attr.ofString(cls, "code").get(e -> e.getCode()).set((e, a) -> e.setCode(a)));
		add(Attr.ofString(cls, "address_street").get(e -> e.getAddress() != null ? e.getAddress().getStreet() : null).set((e, a) -> {
			if (a != null) {
				ensureAddressExists(e);
				e.getAddress().setStreet(a);
			}
		}));
		add(Attr.ofString(cls, "address_street_number").get(e -> e.getAddress() != null ? e.getAddress().getStreetNumber() : null).set((e, a) -> {
			if (a != null) {
				ensureAddressExists(e);
				e.getAddress().setStreetNumber(a);
			}
		}));
		add(Attr.ofString(cls, "address_city").get(e -> e.getAddress() != null ? e.getAddress().getCity() : null).set((e, a) -> {
			if (a != null) {
				ensureAddressExists(e);
				e.getAddress().setCity(a);
			}
		}));
		add(Attr.ofString(cls, "address_postal_code").get(e -> e.getAddress() != null ? e.getAddress().getPostalCode() : null).set((e, a) -> {
			if (a != null) {
				ensureAddressExists(e);
				e.getAddress().setPostalCode(a);
			}
		}));
		customer_id = add(Attr.ofInteger(cls, "customer_id").get(e -> e.getCustomerId()).set((e, a) -> e.setCustomerId(a)));
		add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()).set((e, a) -> e.setCreationTime(a)));
		add(Attr.ofInteger(cls, "bonus_points").get(e -> e.getBonusPoints()).set((e, a) -> e.setBonusPoints(a)));
		add(Attr.ofBigDecimal(cls, "previous_year_consumption").get(e -> e.getPreviousYearConsumption()).set((e, a) -> e.setPreviousYearConsumption(a)));
		add(Attr.ofBigDecimal(cls, "current_year_consumption").get(e -> e.getCurrentYearConsumption()).set((e, a) -> e.setCurrentYearConsumption(a)));
		add(Attr.ofBigDecimal(cls, "consumption_diff").get(e -> e.getConsumptionDiff()).set((e, a) -> e.setConsumptionDiff(a)));
		add(Attr.ofBigDecimal(cls, "voucher_discount").get(e -> e.getVoucherDiscount()).set((e, a) -> e.setVoucherDiscount(a)));
		add(Attr.ofInteger(cls, "benefit_years").get(e -> e.getBenefitYears()).set((e, a) -> e.setBenefitYears(a)));
	}

	public static SupplyPointMapper getInstance() {
		return INSTANCE;
	}
	@Override
	public String getDataSet() {
		return DB_TABLE_NAME;
	}

	@Override
	public SupplyPoint createEntity() {
		return new SupplyPoint();
	}

	@Override
	public List<Condition> composeFilterConditions(SupplyPointFilter filter) {
		List<Condition> conditions = new ArrayList<>();
		if (filter.getId() != null) {
			conditions.add(Conditions.eq(id, filter.getId()));
		}
		if (filter.getCustomerId() != null) {
			conditions.add(Conditions.eq(customer_id, filter.getCustomerId()));
		}
		if (filter.getCustomerIds() != null) {
			conditions.add(Conditions.in(customer_id, filter.getCustomerIds()));
		}
		return conditions;
	}

	protected void ensureAddressExists(SupplyPoint sp) {
		if (sp.getAddress() == null) {
			sp.setAddress(new Address());
		}
	}
}
