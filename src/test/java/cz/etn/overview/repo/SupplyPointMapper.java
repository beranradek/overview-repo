/*
 * Created on 17. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.domain.Address;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.mapper.AbstractEntityMapper;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.AttributeSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping of supply point attributes to database fields.
 * @author Radek Beran
 */
public enum SupplyPointMapper implements AbstractEntityMapper<SupplyPoint, SupplyPointFilter>, Attribute<SupplyPoint, Object> {
	id {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getId();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setId(attributeSource.get(Integer.class, attributeName));
			return instance;
		}
		
		@Override
		public boolean isPrimary() {
			return true;
		}
	},
	code {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getCode();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCode(attributeSource.get(String.class, attributeName));
			return instance;
		}
	},
	address_street {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getStreet() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.get(String.class, attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setStreet(value);
			}
			return instance;
		}
	},
	address_street_number {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getStreetNumber() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.get(String.class, attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setStreetNumber(value);
			}
			return instance;
		}
	},
	address_city {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getCity() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.get(String.class, attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setCity(value);
			}
			return instance;
		}
	},
	address_postal_code {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getPostalCode() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.get(String.class, attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setPostalCode(value);
			}
			return instance;
		}
	},
	customer_id {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getCustomerId();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCustomerId(attributeSource.get(Long.class, attributeName));
			return instance;
		}
	},
	creation_time {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getCreationTime();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCreationTime(attributeSource.get(Instant.class, attributeName));
			return instance;
		}
	},
	bonus_points {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getBonusPoints();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setBonusPoints(attributeSource.get(Integer.class, attributeName));
			return instance;
		}
	},
	previous_year_consumption {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getPreviousYearConsumption();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setPreviousYearConsumption(attributeSource.get(BigDecimal.class, attributeName));
			return instance;
		}
	},
	current_year_consumption {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getCurrentYearConsumption();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCurrentYearConsumption(attributeSource.get(BigDecimal.class, attributeName));
			return instance;
		}
	},
	consumption_diff {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getConsumptionDiff();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setConsumptionDiff(attributeSource.get(BigDecimal.class, attributeName));
			return instance;
		}
	},
	voucher_discount {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getVoucherDiscount();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setVoucherDiscount(attributeSource.get(BigDecimal.class, attributeName));
			return instance;
		}
	},
	benefit_years {
		@Override
		public Object getValue(SupplyPoint instance) {
			return instance.getBenefitYears();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setBenefitYears(attributeSource.get(Integer.class, attributeName));
			return instance;
		}
	};
	
	private static final String DB_TABLE_NAME = "voucher_supply_point";
	
	public static final SupplyPointMapper INSTANCE = id; // any enum constant will suffice here
	
	@Override
	public Attribute<SupplyPoint, Object>[] getAttributes() {
		return values();
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
	public String getName() {
		return name();
	}

	@Override
	public List<FilterCondition> composeFilterConditions(SupplyPointFilter filter) {
		List<FilterCondition> conditions = new ArrayList<>();
		if (filter.getId() != null) {
			conditions.add(FilterCondition.eq(id, filter.getId()));
		}
		if (filter.getCustomerId() != null) {
			conditions.add(FilterCondition.eq(customer_id, filter.getCustomerId()));
		}
		if (filter.getCustomerIds() != null) {
			conditions.add(FilterCondition.in(customer_id, CollectionFuns.toObjectList(filter.getCustomerIds())));
		}
		return conditions;
	}
	
	protected void ensureAddressExists(SupplyPoint sp) {
		if (sp.getAddress() == null) {
			sp.setAddress(new Address());
		}
	}
}
