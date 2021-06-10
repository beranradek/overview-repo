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

import org.xbery.overview.domain.Address;
import org.xbery.overview.domain.SupplyPoint;
import org.xbery.overview.domain.SupplyPointFilter;
import org.xbery.overview.filter.Condition;
import org.xbery.overview.mapper.Attr;
import org.xbery.overview.mapper.Attribute;
import org.xbery.overview.mapper.AttributeSource;
import org.xbery.overview.mapper.DynamicEntityMapper;
import org.xbery.overview.repo.Conditions;

import java.math.BigDecimal;
import java.time.Instant;
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
	public final Attribute<SupplyPoint, String> address_street;
	public final Attribute<SupplyPoint, String> address_street_number;
	public final Attribute<SupplyPoint, String> address_city;
	public final Attribute<SupplyPoint, String> address_postal_code;
	public final Attribute<SupplyPoint, Integer> customer_id;
	public final Attribute<SupplyPoint, Instant> creation_time;
	public final Attribute<SupplyPoint, Integer> bonus_points;
	public final Attribute<SupplyPoint, BigDecimal> previous_year_consumption;
	public final Attribute<SupplyPoint, BigDecimal> current_year_consumption;
	public final Attribute<SupplyPoint, BigDecimal> consumption_diff;
	public final Attribute<SupplyPoint, BigDecimal> voucher_discount;
	public final Attribute<SupplyPoint, Integer> benefit_years;

	protected SupplyPointMapper() {
		id = add(Attr.ofInteger(cls, "id").primary().get(e -> e.getId()));
		code = add(Attr.ofString(cls, "code").get(e -> e.getCode()));
		address_street = add(Attr.ofString(cls, "address_street").get(e -> e.getAddress() != null ? e.getAddress().getStreet() : null));
		address_street_number = add(Attr.ofString(cls, "address_street_number").get(e -> e.getAddress() != null ? e.getAddress().getStreetNumber() : null));
		address_city = add(Attr.ofString(cls, "address_city").get(e -> e.getAddress() != null ? e.getAddress().getCity() : null));
		address_postal_code = add(Attr.ofString(cls, "address_postal_code").get(e -> e.getAddress() != null ? e.getAddress().getPostalCode() : null));
		customer_id = add(Attr.ofInteger(cls, "customer_id").get(e -> e.getCustomerId()));
		creation_time = add(Attr.ofInstant(cls, "creation_time").get(e -> e.getCreationTime()));
		bonus_points = add(Attr.ofInteger(cls, "bonus_points").get(e -> e.getBonusPoints()));
		previous_year_consumption = add(Attr.ofBigDecimal(cls, "previous_year_consumption").get(e -> e.getPreviousYearConsumption()));
		current_year_consumption = add(Attr.ofBigDecimal(cls, "current_year_consumption").get(e -> e.getCurrentYearConsumption()));
		consumption_diff = add(Attr.ofBigDecimal(cls, "consumption_diff").get(e -> e.getConsumptionDiff()));
		voucher_discount = add(Attr.ofBigDecimal(cls, "voucher_discount").get(e -> e.getVoucherDiscount()));
		benefit_years = add(Attr.ofInteger(cls, "benefit_years").get(e -> e.getBenefitYears()));
	}

	public static SupplyPointMapper getInstance() {
		return INSTANCE;
	}
	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}

	@Override
	public SupplyPoint createEntity(AttributeSource attributeSource, List<Attribute<SupplyPoint, ?>> attributes, String aliasPrefix) {
		SupplyPoint p = new SupplyPoint();
		p.setId(id.getValueFromSource(attributeSource, aliasPrefix));
		p.setCode(code.getValueFromSource(attributeSource, aliasPrefix));
		Address address = new Address();
		address.setStreet(address_street.getValueFromSource(attributeSource, aliasPrefix));
		address.setStreetNumber(address_street_number.getValueFromSource(attributeSource, aliasPrefix));
		address.setCity(address_city.getValueFromSource(attributeSource, aliasPrefix));
		address.setPostalCode(address_postal_code.getValueFromSource(attributeSource, aliasPrefix));
		p.setAddress(address);
		p.setCustomerId(customer_id.getValueFromSource(attributeSource, aliasPrefix));
		p.setCreationTime(creation_time.getValueFromSource(attributeSource, aliasPrefix));
		p.setBonusPoints(bonus_points.getValueFromSource(attributeSource, aliasPrefix));
		p.setPreviousYearConsumption(previous_year_consumption.getValueFromSource(attributeSource, aliasPrefix));
		p.setCurrentYearConsumption(current_year_consumption.getValueFromSource(attributeSource, aliasPrefix));
		p.setConsumptionDiff(consumption_diff.getValueFromSource(attributeSource, aliasPrefix));
		p.setVoucherDiscount(voucher_discount.getValueFromSource(attributeSource, aliasPrefix));
		p.setBenefitYears(benefit_years.getValueFromSource(attributeSource, aliasPrefix));
		return p;
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
}
