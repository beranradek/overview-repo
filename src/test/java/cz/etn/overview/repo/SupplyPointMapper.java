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
import cz.etn.overview.mapper.AbstractEntityMapper;
import cz.etn.overview.mapper.AttributeMapping;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;

/**
 * Mapping of supply point attributes to database fields.
 * @author Radek Beran
 */
public enum SupplyPointMapper implements AbstractEntityMapper<SupplyPoint>, AttributeMapping<SupplyPoint> {
	id {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getId();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setId(attributeSource.getInteger(attributeName));
			return instance;
		}
		
		@Override
		public boolean isPrimaryAttribute() {
			return true;
		}
	},
	code {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getCode();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCode(attributeSource.getString(attributeName));
			return instance;
		}
	},
	address_street {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getStreet() : null; 
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.getString(attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setStreet(value);
			}
			return instance;
		}
	},
	address_street_number {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getStreetNumber() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.getString(attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setStreetNumber(value);
			}
			return instance;
		}
	},
	address_city {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getCity() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.getString(attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setCity(value);
			}
			return instance;
		}
	},
	address_postal_code {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getAddress() != null ? instance.getAddress().getPostalCode() : null;
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			String value = attributeSource.getString(attributeName);
			if (value != null) {
				ensureAddressExists(instance);
				instance.getAddress().setPostalCode(value);
			}
			return instance;
		}
	},
	customer_id {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getCustomerId();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCustomerId(attributeSource.getLong(attributeName));
			return instance;
		}
	},
	creation_time {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getCreationTime();
		}
		
		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCreationTime(attributeSource.getInstant(attributeName));
			return instance;
		}
	},
	bonus_points {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getBonusPoints();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setBonusPoints(attributeSource.getInteger(attributeName));
			return instance;
		}
	},
	previous_year_consumption {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getPreviousYearConsumption();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setPreviousYearConsumption(attributeSource.getBigDecimal(attributeName));
			return instance;
		}
	},
	current_year_consumption {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getCurrentYearConsumption();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setCurrentYearConsumption(attributeSource.getBigDecimal(attributeName));
			return instance;
		}
	},
	consumption_diff {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getConsumptionDiff();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setConsumptionDiff(attributeSource.getBigDecimal(attributeName));
			return instance;
		}
	},
	voucher_discount {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getVoucherDiscount();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setVoucherDiscount(attributeSource.getBigDecimal(attributeName));
			return instance;
		}
	},
	benefit_years {
		@Override
		public Object getAttributeValue(SupplyPoint instance) {
			return instance.getBenefitYears();
		}

		@Override
		public SupplyPoint entityWithAttribute(SupplyPoint instance, AttributeSource attributeSource, String attributeName) {
			instance.setBenefitYears(attributeSource.getInteger(attributeName));
			return instance;
		}
	};
	
	private static final String DB_TABLE_NAME = "voucher_supply_point";
	
	public static final EntityMapper<SupplyPoint> INSTANCE = id; // any enum constant will suffice here
	
	@Override
	public AttributeMapping<SupplyPoint>[] getAttributeMappings() {
		return values();
	}

	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public SupplyPoint createEntity() {
		return new SupplyPoint();
	}
	
	@Override
	public String getAttributeName() {
		return name();
	}
	
	protected void ensureAddressExists(SupplyPoint sp) {
		if (sp.getAddress() == null) {
			sp.setAddress(new Address());
		}
	}
}
