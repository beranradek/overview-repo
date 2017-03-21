/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import cz.etn.overview.domain.VoucherCustomer;
import cz.etn.overview.mapper.AbstractEntityMapper;
import cz.etn.overview.mapper.Attribute;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;

/**
 * Mapping of voucher customer attributes to database fields.
 * @author Radek Beran
 */
public enum VoucherCustomerMapper implements AbstractEntityMapper<VoucherCustomer>, Attribute<VoucherCustomer, Object> {
	
	id {
		@Override
		public Object getValue(VoucherCustomer instance) {
			return instance.getId();
		}

		@Override
		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
			instance.setId(attributeSource.get(Integer.class, attributeName));
			return instance;
		}
		
		@Override
		public boolean isPrimary() {
			return true;
		}
	};
//	creation_time {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getCreationTime();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setCreationTime(attributeSource.get(Instant.class, attributeName));
//			return instance;
//		}
//	},
//	email {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getEmail();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setEmail(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	first_name {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getFirstName();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setFirstName(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	last_name {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getLastName();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setLastName(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	salutation {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getSalutation();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setSalutation(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	business_partner_code {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getBusinessPartnerCode();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setBusinessPartnerCode(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	discount_email_type {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			if (instance.getDiscountEmailType() == null) return null;
//			return instance.getDiscountEmailType().name();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			String dbValue = attributeSource.getString(attributeName);
//			DiscountEmailType emailType = null;
//			if (dbValue != null && !dbValue.isEmpty()) {
//				emailType = DiscountEmailType.valueOf(dbValue);
//			}
//			instance.setDiscountEmailType(emailType);
//			return instance;
//		}
//	},
//	email_sent_time {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getEmailSentTime();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setEmailSentTime(attributeSource.getInstant(attributeName));
//			return instance;
//		}
//	},
//	email_sending_state {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			if (instance.getEmailSendingState() == null) return null;
//			return instance.getEmailSendingState().name();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			String dbValue = attributeSource.getString(attributeName);
//			SendingState emailSendingState = null;
//			if (dbValue != null && !dbValue.isEmpty()) {
//				emailSendingState = SendingState.valueOf(dbValue);
//			}
//			instance.setEmailSendingState(emailSendingState);
//			return instance;
//		}
//	},
//	email_text {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getEmailText();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setEmailText(attributeSource.getString(attributeName));
//			return instance;
//		}
//	},
//	import_file_name {
//		@Override
//		public Object getValue(VoucherCustomer instance) {
//			return instance.getImportFileName();
//		}
//
//		@Override
//		public VoucherCustomer entityWithAttribute(VoucherCustomer instance, AttributeSource attributeSource, String attributeName) {
//			instance.setImportFileName(attributeSource.getString(attributeName));
//			return instance;
//		}
//	};
	
	private static final String DB_TABLE_NAME = "voucher_customer";
	
	public static final EntityMapper<VoucherCustomer> INSTANCE = id; // any enum constant will suffice here
	
	@Override
	public Attribute<VoucherCustomer, Object>[] getAttributes() {
		return values();
	}
	
	@Override
	public String getTableName() {
		return DB_TABLE_NAME;
	}
	
	@Override
	public VoucherCustomer createEntity() {
		return new VoucherCustomer();
	}
	
	@Override
	public String getName() {
		return name();
	}
}
