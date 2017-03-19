/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.VoucherCustomerTestData;
import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.domain.VoucherCustomer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for {@link VoucherCustomerRepositoryImpl}.
 * @author Radek Beran
 */
public class VoucherCustomerRepositoryImplTest {

	private final DataSource dataSource;
	private final VoucherCustomerTestData testData;
	
	public VoucherCustomerRepositoryImplTest() {
		VoucherTestDb testDb = new VoucherTestDb();
		this.dataSource = testDb.createDataSource();
		this.testData = new VoucherCustomerTestData();
	}

	@Test
	public void createFindDeleteCustomer() {
		VoucherCustomerRepository repo = new VoucherCustomerRepositoryImpl(dataSource, new SupplyPointRepositoryImpl(dataSource));
		VoucherCustomer customer = testData.newVoucherCustomer("jan.novak@etnetera.cz", "Jan", "Novak");
		customer.setSupplyPoints(new ArrayList<>()); // Supply points from test data are now not loaded by findById
		customer.setVoucher(null); // Voucher from test data are now not loaded by findById

		VoucherCustomer customerCreated = repo.create(customer, true);
		assertTrue("Created customer has id assigned", customer.getId() != null);
		assertTrue("Created customer " + customerCreated + " equals customer to store " + customer, EqualsBuilder.reflectionEquals(customer, customerCreated));
		
		assertEquals(customerCreated.getId(), customer.getId());
		Optional<VoucherCustomer> foundCustomerOpt = repo.findById(customer.getId());
		assertTrue("Found customer " + foundCustomerOpt.get() + " was not equal to customer to store " + customer, EqualsBuilder.reflectionEquals(customer, foundCustomerOpt.get()));
		
		repo.delete(customer.getId());
		Optional<VoucherCustomer> foundCustomerAfterDeleteOpt = repo.findById(customer.getId());
		assertFalse("Customer should not be present in database after deletion", foundCustomerAfterDeleteOpt.isPresent());
	}

}
