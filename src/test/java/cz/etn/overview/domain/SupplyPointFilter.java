/*
 * Created on 23. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */
package cz.etn.overview.domain;

import java.util.List;

import cz.etn.overview.Filter;

/**
 * Filter of voucher customers.
 * @author Radek Beran
 */
public class SupplyPointFilter implements Filter {
	private static final long serialVersionUID = 4590219796927704612L;
	private Long id;
	
	private Long customerId;
	
	private List<Integer> customerIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public List<Integer> getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(List<Integer> customerIds) {
		this.customerIds = customerIds;
	}

	@Override
	public String toString() {
		return "SupplyPointFilter [id=" + id + ", customerId=" + customerId + "]";
	}
}
