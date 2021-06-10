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
package org.xbery.overview.sql.repo;

import org.xbery.overview.Overview;
import org.xbery.overview.VoucherTestDb;
import org.xbery.overview.data.CustomerTestData;
import org.xbery.overview.data.VoucherTestData;
import org.xbery.overview.domain.Customer;
import org.xbery.overview.domain.CustomerFilter;
import org.xbery.overview.domain.Voucher;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for {@link CustomerRepositoryImpl}.
 * @author Radek Beran
 */
public class CustomerRepositoryImplTest {

	private final DataSource dataSource;
	private final VoucherTestData voucherTestData;
	private final CustomerTestData customerTestData;
	
	public CustomerRepositoryImplTest() {
		this.dataSource = new VoucherTestDb().createDataSource();
		this.customerTestData = new CustomerTestData();
		this.voucherTestData = new VoucherTestData();
	}

	@Test
	public void createFindDeleteCustomer() {
		CustomerRepository repo = createCustomerRepository();
		try {
			Customer customer = customerTestData.createCustomerWithSupplyPoints("jan.novak@gmail.com", "Jan", "Novak");
			customer.setSupplyPoints(null); // Supply points from test data are not loaded by findById
			customer.setVoucher(null); // Voucher from test data are now not loaded by findById

			Customer customerCreated = repo.create(customer, true);
			assertTrue("Created customer has id assigned", customerCreated.getId() != null);
			customer.setId(customerCreated.getId()); // so the entities are now equal
			customer.setCreationTime(customerCreated.getCreationTime()); // so the entities are now equal (precision differs)
			assertTrue("Created customer " + customerCreated + " equals customer to store " + customer, EqualsBuilder.reflectionEquals(customer, customerCreated));

			Optional<Customer> foundCustomerOpt = repo.findById(customer.getId());
			assertTrue("Found customer " + foundCustomerOpt.get() + " was not equal to customer to store " + customer, EqualsBuilder.reflectionEquals(customer, foundCustomerOpt.get()));

			repo.delete(customer.getId());
			Optional<Customer> foundCustomerAfterDeleteOpt = repo.findById(customer.getId());
			assertFalse("Customer should not be present in database after deletion", foundCustomerAfterDeleteOpt.isPresent());
		} finally {
			// TODO RBe: Clear VoucherTestDb
			// Delete all records after test
			repo.deleteByFilter(new CustomerFilter());
		}
	}

	@Test
	public void findCustomerLeftJoinVoucher() {
		CustomerRepository customerRepo = createCustomerRepository();
		VoucherRepository voucherRepo = createVoucherRepository();

		try {
			// Customer has at most one generated voucher
			Customer jan = customerTestData.createCustomer("jan.novak@gmail.com", "Jan", "Novak");
			jan = customerRepo.create(jan, true);

			Voucher janVoucher = voucherTestData.createVoucher("XCVB", "" + jan.getId());
			janVoucher = voucherRepo.create(janVoucher, false);
			jan.setVoucher(janVoucher);

			Customer martina = customerTestData.createCustomer("martina.vesela@gmail.com", "Martina", "Vesela");
			martina = customerRepo.create(martina, true);
			// Martina will remain without voucher

			List<Customer> customers = customerRepo.findWithVoucher(Overview.empty());
			// Customers are sorted by their id (default ordering)
			assertEquals(2, customers.size());
			Customer janLoaded = customers.get(0);
			assertEquals(jan.getEmail(), janLoaded.getEmail());
			Customer martinaLoaded = customers.get(1);
			assertEquals(martina.getEmail(), martinaLoaded.getEmail());

			assertTrue("Jan has one voucher", janLoaded.getVoucher() != null && janLoaded.getVoucher().getCode() != null);
			jan.setCreationTime(janLoaded.getCreationTime()); // so the entities are now equal (precision differs)
			assertTrue(janLoaded + " equals " + jan, EqualsBuilder.reflectionEquals(janLoaded, jan));
			janVoucher.setCreationTime(janLoaded.getVoucher().getCreationTime()); // so the entities are now equal (precision differs)
			janVoucher.setValidFrom(janLoaded.getVoucher().getValidFrom()); // so the entities are now equal (precision differs)
			assertTrue(janVoucher + " equals " + janLoaded.getVoucher(), EqualsBuilder.reflectionEquals(janVoucher, janLoaded.getVoucher()));

			assertTrue("Martina has no voucher", martinaLoaded.getVoucher() == null);
			martina.setCreationTime(martinaLoaded.getCreationTime()); // so the entities are now equal (precision differs)
			assertTrue(martinaLoaded + " equals " + martina, EqualsBuilder.reflectionEquals(martinaLoaded, martina));
		} finally {
			// TODO RBe: Clear VoucherTestDb
			// Delete all records after test
			voucherRepo.deleteByFilter(new Object());
			customerRepo.deleteByFilter(new CustomerFilter());
		}
	}

	protected CustomerRepositoryImpl createCustomerRepository() {
		return new CustomerRepositoryImpl(dataSource);
	}

	protected VoucherRepositoryImpl createVoucherRepository() {
		return new VoucherRepositoryImpl(dataSource);
	}

}
