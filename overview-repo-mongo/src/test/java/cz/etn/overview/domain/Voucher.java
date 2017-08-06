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

/**
 * Voucher.
 * @author Radek Beran
 */
public class Voucher {
	private static final long serialVersionUID = 1810293320512995607L;

	/**
	 * Time when the entity was created.
	 */
	private Instant creationTime;

	/**
	 * Unique voucher discount code.
	 */
	private String code;

	/**
	 * Discount price.
	 */
	private BigDecimal discountPrice;

	/**
	 * Time from which the voucher is valid.
	 */
	private Instant validFrom;

	/**
	 * Time to which the voucher is valid.
	 */
	private Instant validTo;

	/**
	 * Time when the voucher was redeemed.
	 */
	private Instant redemptionTime;

	/**
	 * Time when the voucher was invalidated.
	 */
	private Instant invalidationTime;
	
	/**
	 * Reason of voucher invalidation.
	 */
	private String invalidationNote;
	
	/**
	 * Reason of voucher renewal.
	 */
	private String renewalNote;

	/**
	 * Identifier of customer who has redeemed the voucher.
	 */
	private String redeemedBy;
	
	/**
	 * Identifier of customer who has reserved the voucher.
	 */
	private String reservedBy;

	/**
	 * Identifier of business partner in which the voucher applied.
	 */
	private String soldBy;

	/**
	 * Time of invoicing the discount.
	 */
	private Instant invoiceTime;

	/**
	 * Note about invoicing the discount.
	 */
	private String invoiceNote;

	public Instant getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = normalizedVoucherCode(code);
	}

	public BigDecimal getDiscountPrice() {
		return discountPrice;
	}

	public String getDiscountPriceAsString() {
		return discountPrice != null ? discountPrice.setScale(0).toPlainString() : null;
	}

	public void setDiscountPrice(BigDecimal discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Instant getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Instant validFrom) {
		this.validFrom = validFrom;
	}

	public Instant getValidTo() {
		return validTo;
	}

	public void setValidTo(Instant validTo) {
		this.validTo = validTo;
	}

	public Instant getRedemptionTime() {
		return redemptionTime;
	}

	public void setRedemptionTime(Instant redemptionTime) {
		this.redemptionTime = redemptionTime;
	}

	public Instant getInvalidationTime() {
		return invalidationTime;
	}

	public void setInvalidationTime(Instant invalidationTime) {
		this.invalidationTime = invalidationTime;
	}

	public String getInvalidationNote() {
		return invalidationNote;
	}

	public void setInvalidationNote(String invalidationNote) {
		this.invalidationNote = invalidationNote;
	}

	public String getRenewalNote() {
		return renewalNote;
	}

	public void setRenewalNote(String renewalNote) {
		this.renewalNote = renewalNote;
	}

	public String getRedeemedBy() {
		return redeemedBy;
	}

	public void setRedeemedBy(String redeemedBy) {
		this.redeemedBy = redeemedBy;
	}
	
	public String getReservedBy() {
		return reservedBy;
	}
	
	public void setReservedBy(String reservedBy) {
		this.reservedBy = reservedBy;
	}

	public String getSoldBy() {
		return soldBy;
	}

	public void setSoldBy(String soldBy) {
		this.soldBy = soldBy;
	}

	public Instant getInvoiceTime() {
		return invoiceTime;
	}

	public void setInvoiceTime(Instant invoiceTime) {
		this.invoiceTime = invoiceTime;
	}

	public String getInvoiceNote() {
		return invoiceNote;
	}

	public void setInvoiceNote(String invoiceNote) {
		this.invoiceNote = invoiceNote;
	}
	
	public boolean isValid() {
		Instant now = Instant.now();
		return (validFrom == null || !now.isBefore(validFrom)) && (validTo == null || !now.isAfter(validTo)) && !isInvalidated();
	}
	
	public boolean isUsed() {
		return redemptionTime != null;
	}
	
	public boolean isInvalidated() {
		return invalidationTime != null;
	}
	
	public boolean canBeInvalidated() {
		boolean res = isValid() && !isUsed();
		return res;
	}
	
	public boolean canBeInvalidationRestored() {
		boolean res = isInvalidated();
		return res;
	}
	
	/**
	 * Whether this voucher is free for redemption.
	 * @return
	 */
	public boolean isFreeToUse() {
		return isValid() && !isUsed();
	}
	
	public boolean isRedeemed() {
		return redemptionTime != null;
	}

	@Override
	public String toString() {
		return "Voucher [code=" + code + ", discountPrice=" + discountPrice + ", creationTime=" + getCreationTime() + 
				", validFrom=" + validFrom + ", validTo=" + validTo + ", redemptionTime=" + redemptionTime + 
				", invalidationTime=" + invalidationTime + ", redeemedBy=" + redeemedBy +
				", reservedBy=" + reservedBy +
				", soldBy=" + soldBy + ", invoiceTime=" + invoiceTime + ", invoiceNote=" + invoiceNote + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Voucher voucher = (Voucher) o;

		return code != null ? code.equals(voucher.code) : voucher.code == null;

	}

	@Override
	public int hashCode() {
		return code != null ? code.hashCode() : 0;
	}

	/**
	 * Normalizes voucher code to uppercase without leading and trailing spaces.
	 * @param voucherCode
	 * @return
	 */
	public static String normalizedVoucherCode(String voucherCode) {
		if (voucherCode == null) return null;
		return voucherCode.trim().toUpperCase();
	}

}
