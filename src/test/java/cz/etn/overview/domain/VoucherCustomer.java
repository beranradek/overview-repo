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
package cz.etn.overview.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Record about EON customer with his bonus points that can be applied in loyalty program.
 * @author Radek Beran
 */
public class VoucherCustomer extends AbstractEntity<Integer> {

	private Integer id;
	
	private String email;
	
	private String firstName;
	
	private String lastName;
	
	private String salutation;
	
	/**
	 * Business partner code for grouping records of one customer with more supply points together.
	 */
	private String businessPartnerCode;
	
	private List<SupplyPoint> supplyPoints;
	
	/**
	 * Unique voucher generated for customer if customer can gain discount.
	 */
	private Voucher voucher;

	/**
	 * Type of already SENT discount e-mail.
	 */
	private DiscountEmailType discountEmailType;
	
	private Instant emailSentTime;
	
	private SendingState emailSendingState;
	
	private String emailText;
	
	/**
	 * Name of import file. 
	 */
	private String importFileName;
	
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getBusinessPartnerCode() {
		return businessPartnerCode;
	}

	public void setBusinessPartnerCode(String businessPartnerCode) {
		this.businessPartnerCode = businessPartnerCode;
	}

	public List<SupplyPoint> getSupplyPoints() {
		return supplyPoints;
	}

	public void setSupplyPoints(List<SupplyPoint> supplyPoints) {
		this.supplyPoints = supplyPoints;
	}
	
	public Voucher getVoucher() {
		return voucher;
	}
	
	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

	public DiscountEmailType getDiscountEmailType() {
		return discountEmailType;
	}

	public void setDiscountEmailType(DiscountEmailType discountEmailType) {
		this.discountEmailType = discountEmailType;
	}
	
	public Instant getEmailSentTime() {
		return emailSentTime;
	}
	
	public void setEmailSentTime(Instant emailSentTime) {
		this.emailSentTime = emailSentTime;
	}

	public SendingState getEmailSendingState() {
		return emailSendingState;
	}

	public void setEmailSendingState(SendingState emailSendingState) {
		this.emailSendingState = emailSendingState;
	}

	public String getEmailText() {
		return emailText;
	}

	public void setEmailText(String emailText) {
		this.emailText = emailText;
	}

	public String getImportFileName() {
		return importFileName;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

	public DiscountEmailType resolveDiscountEmailType(List<SupplyPoint> sp) {
		DiscountEmailType emailType = DiscountEmailType.NO_DISCOUNT;
		if (hasPositiveVoucherDiscount() && sp != null && !sp.isEmpty()) {
			int supplyPointsWithDiscountCount = 0;
			for (SupplyPoint s : sp) {
				if (s.hasDiscount()) {
					supplyPointsWithDiscountCount++;
				}
			}
			if (supplyPointsWithDiscountCount > 1) {
				emailType = DiscountEmailType.MORE_SUPPLY_POINTS_DISCOUNT;
			} else if (supplyPointsWithDiscountCount == 1) {
				emailType = DiscountEmailType.ONE_SUPPLY_POINT_DISCOUNT;
			}
		}
		return emailType;
	}
	
	public BigDecimal computeDiscount() {
		List<SupplyPoint> sp = getSupplyPoints();
		BigDecimal discount = BigDecimal.ZERO;
		if (sp != null) {
			for (SupplyPoint supplyPoint : sp) {
				discount = discount.add(supplyPoint.getVoucherDiscount());
			}
		}
		return discount;
	}

	private boolean hasPositiveVoucherDiscount() {
		return getVoucher() != null && getVoucher().getDiscountPrice() != null && getVoucher().getDiscountPrice().compareTo(BigDecimal.ZERO) > 0;
	}

	@Override
	public String toString() {
		return "VoucherCustomer [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + ", salutation=" + salutation + ", businessPartnerCode=" + businessPartnerCode + ", supplyPoints=" + supplyPoints + ", voucher="
			+ voucher + ", discountEmailType=" + discountEmailType + ", emailSentTime=" + emailSentTime + ", emailSendingState=" + emailSendingState + ", importFileName=" + importFileName + "]";
	}
	
}
