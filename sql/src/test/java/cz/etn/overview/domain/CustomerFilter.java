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

import java.io.Serializable;
import java.util.List;

/**
 * Filter of voucher customers.
 * @author Radek Beran
 */
public class CustomerFilter implements Serializable {
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
