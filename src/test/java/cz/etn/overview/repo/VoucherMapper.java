/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.AbstractEntityMapper;
import cz.etn.overview.mapper.AttributeMapping;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;

/**
 * Mapping of voucher attributes to database attributes.
 * @author Radek Beran
 */
public enum VoucherMapper implements AbstractEntityMapper<Voucher>, AttributeMapping<Voucher, Object> {
	
	code {
		@Override
		public Object getValue(Voucher instance) {
			return instance.getCode();
		}

		@Override
		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
			instance.setCode(attributeSource.get(String.class, attributeName));
			return instance;
		}

		@Override
		public boolean isPrimary() {
			return true;
		}
	},
//	creation_time {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getCreationTime();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setCreationTime(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	discount_price {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getDiscountPrice();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setDiscountPrice(attributeSource.getBigDecimal(attributeName));
//			return instance;
//		}
//	},
//	valid_from {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getValidFrom();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setValidFrom(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	valid_to {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getValidTo();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setValidTo(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	redemption_time {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getRedemptionTime();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setRedemptionTime(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	invalidation_time {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getInvalidationTime();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setInvalidationTime(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	invalidation_note {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getInvalidationNote();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setInvalidationNote(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	renewal_note {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getRenewalNote();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setRenewalNote(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
	reserved_by {
		@Override
		public Object getValue(Voucher instance) {
			return instance.getReservedBy();
		}

		@Override
		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
			instance.setReservedBy(attributeSource.get(String.class, attributeName));
			return instance;
		}
	};
//	redeemed_by {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getRedeemedBy();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setRedeemedBy(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	sold_by {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getSoldBy();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setSoldBy(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	invoice_time {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getInvoiceTime();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setInvoiceTime(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	invoice_note {
//		@Override
//		public Object getValue(Voucher instance) {
//			return instance.getInvoiceNote();
//		}
//
//		@Override
//		public Voucher entityWithAttribute(Voucher instance, AttributeSource attributeSource, String attributeName) {
//			instance.setInvoiceNote(attributeSource.getString(attributeName));
//			return instance;
//		}
//	};
	
	private static final String DB_TABLE_NAME = "voucher";
	
	public static final EntityMapper<Voucher> INSTANCE = code; // any enum constant will suffice here
	
	@Override
	public AttributeMapping<Voucher, Object>[] getAttributeMappings() {
		return values();
	}
	
	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public Voucher createEntity() {
		return new Voucher();
	}
	
	@Override
	public String getName() {
		return name();
	}
}
