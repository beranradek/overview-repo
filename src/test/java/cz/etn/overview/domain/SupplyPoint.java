/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.domain;

import java.math.BigDecimal;

/**
 * Supply point ("odberne misto" - OM).
 * @author Radek Beran
 */
public final class SupplyPoint extends AbstractEntity<Integer> {
	
	private Integer id;
	
	/**
	 * Number of supply point.
	 */
	private String code;
	
	/**
	 * Customer owning this supply point.
	 */
	private Long customerId;
	
	private Address address;
	
	/**
	 * Number of total computed bonus points.
	 */
	private Integer bonusPoints;
	
	/**
	 * Consumption of MWh in previous year.
	 */
	private BigDecimal previousYearConsumption;
	
	/**
	 * Consumption of MWh in current year.
	 */
	private BigDecimal currentYearConsumption;
	
	/**
	 * Consumption of MWh in last year minus current year.
	 * If it is positive (there were some spared hours in current year), customer will gain a discount voucher.  
	 */
	private BigDecimal consumptionDiff;
	
	/**
	 * Imported voucher discount.
	 */
	private BigDecimal voucherDiscount;
	
	/**
	 * Number of years in Benefit program.
	 */
	private Integer benefitYears;
	
	public SupplyPoint() {
	}
	
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public Long getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getBonusPoints() {
		return bonusPoints;
	}

	public void setBonusPoints(Integer bonusPoints) {
		this.bonusPoints = bonusPoints;
	}
	
	public BigDecimal getPreviousYearConsumption() {
		return previousYearConsumption;
	}
	
	public String getPreviousYearConsumptionAsString() {
		return previousYearConsumption != null ? previousYearConsumption.setScale(3).toPlainString() : null;
	}

	public void setPreviousYearConsumption(BigDecimal previousYearConsumption) {
		this.previousYearConsumption = previousYearConsumption;
	}

	public BigDecimal getCurrentYearConsumption() {
		return currentYearConsumption;
	}
	
	public String getCurrentYearConsumptionAsString() {
		return currentYearConsumption != null ? currentYearConsumption.setScale(3).toPlainString() : null;
	}

	public void setCurrentYearConsumption(BigDecimal currentYearConsumption) {
		this.currentYearConsumption = currentYearConsumption;
	}

	public BigDecimal getConsumptionDiff() {
		return consumptionDiff;
	}
	
	public String getConsumptionDiffAsString() {
		return consumptionDiff != null ? consumptionDiff.setScale(3).toPlainString() : null; 
	}
	
	public String getConsumptionDiffKWhAsString() {
		return consumptionDiff != null ? consumptionDiff.multiply(BigDecimal.valueOf(1000L)).setScale(0).toPlainString() : null; 
	}

	public void setConsumptionDiff(BigDecimal consumptionDiff) {
		this.consumptionDiff = consumptionDiff;
	}

	public BigDecimal getVoucherDiscount() {
		return voucherDiscount != null ? voucherDiscount : BigDecimal.ZERO;
	}
	
	public String getVoucherDiscountAsString() {
		return voucherDiscount != null ? voucherDiscount.setScale(0).toPlainString() : null;
	}

	public void setVoucherDiscount(BigDecimal voucherDiscount) {
		this.voucherDiscount = voucherDiscount;
	}

	public Integer getBenefitYears() {
		return benefitYears;
	}

	public void setBenefitYears(Integer benefitYears) {
		this.benefitYears = benefitYears;
	}
	
	public boolean hasDiscount() {
		return getVoucherDiscount() != null && getVoucherDiscount().compareTo(BigDecimal.ZERO) > 0;
	}

	@Override
	public String toString() {
		return "SupplyPoint [id=" + id + ", code=" + code + "]";
	}
}