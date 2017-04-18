/*
 * Created on 21. 2. 2017 Copyright (c) 2017 Etnetera, a.s. All rights reserved. Intended for
 * internal use only. http://www.etnetera.cz
 */

package cz.etn.overview.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Filter of voucher customers.
 * @author Radek Beran
 */
public class VoucherCustomerFilter implements Serializable {
	private static final long serialVersionUID = 4590219796927704612L;
	private Integer id;
	
	private String importFileName;
	
	private List<Long> customerIds;
	
	private String soldBy;
	
	private Boolean latestInvoiceOfSeller;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImportFileName() {
		return importFileName;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

	public String getSoldBy() {
		return soldBy;
	}

	public void setSoldBy(String soldBy) {
		this.soldBy = soldBy;
	}

	public List<Long> getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(List<Long> customerIds) {
		this.customerIds = customerIds;
	}

	public Boolean getLatestInvoiceOfSeller() {
		return latestInvoiceOfSeller;
	}

	public void setLatestInvoiceOfSeller(Boolean latestInvoiceOfSeller) {
		this.latestInvoiceOfSeller = latestInvoiceOfSeller;
	}
}
