/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Overview;
import cz.etn.overview.VoucherCustomerTestData;
import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.domain.VoucherCustomer;
import cz.etn.overview.domain.VoucherCustomerFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
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
		VoucherCustomerRepository repo = createVoucherCustomerRepository();
		VoucherCustomer customer = testData.newVoucherCustomer("jan.novak@etnetera.cz", "Jan", "Novak");
		customer.setSupplyPoints(null); // Supply points from test data are not loaded by findById
		customer.setVoucher(null); // Voucher from test data are now not loaded by findById

		VoucherCustomer customerCreated = repo.create(customer, true);
		assertTrue("Created customer has id assigned", customerCreated.getId() != null);
		customer.setId(customerCreated.getId()); // so the entities are now equal
		assertTrue("Created customer " + customerCreated + " equals customer to store " + customer, EqualsBuilder.reflectionEquals(customer, customerCreated));

		Optional<VoucherCustomer> foundCustomerOpt = repo.findById(customer.getId());
		assertTrue("Found customer " + foundCustomerOpt.get() + " was not equal to customer to store " + customer, EqualsBuilder.reflectionEquals(customer, foundCustomerOpt.get()));
		
		repo.delete(customer.getId());
		Optional<VoucherCustomer> foundCustomerAfterDeleteOpt = repo.findById(customer.getId());
		assertFalse("Customer should not be present in database after deletion", foundCustomerAfterDeleteOpt.isPresent());
	}

	@Test
	public void findCustomerLeftJoinVoucher() {
		VoucherCustomerRepositoryImpl customerRepo = createVoucherCustomerRepository();
		VoucherRepositoryImpl voucherRepo = createVoucherRepository();

		// Customer has at most one generated voucher
		VoucherCustomer jan = testData.newVoucherCustomer("jan.novak@etnetera.cz", "Jan", "Novak");
		jan = customerRepo.create(jan, true);

		Voucher janVoucher = testData.createVoucher("XCVB", "" + jan.getId());
		janVoucher = voucherRepo.create(janVoucher, false);
		jan.setVoucher(janVoucher);
		jan.setSupplyPoints(new ArrayList<>());

		VoucherCustomer martina = testData.newVoucherCustomer("martina.vesela@etnetera.cz", "Martina", "Vesela");
		martina = customerRepo.create(martina, true);
		martina.setSupplyPoints(new ArrayList<>());
		// without voucher

		List<VoucherCustomer> customers = customerRepo.findByOverview(new Overview<>(null, null, null));
		// Customers are sorted by their id (default ordering)
		assertEquals(2, customers.size());
		VoucherCustomer janLoaded = customers.get(0);
		assertEquals(jan.getEmail(), janLoaded.getEmail());
		VoucherCustomer martinaLoaded = customers.get(1);
		assertEquals(martina.getEmail(), martinaLoaded.getEmail());

		assertTrue("Jan has one voucher", janLoaded.getVoucher() != null && janLoaded.getVoucher().getCode() != null);
		assertTrue(janLoaded + " equals " + jan, EqualsBuilder.reflectionEquals(janLoaded, jan));
		assertTrue(janVoucher + " equals " + janLoaded.getVoucher(), EqualsBuilder.reflectionEquals(janVoucher, janLoaded.getVoucher()));

		assertTrue("Martina has no voucher", martinaLoaded.getVoucher() == null);
		assertTrue(martinaLoaded + " equals " + martina, EqualsBuilder.reflectionEquals(martinaLoaded, martina));
	}

	protected VoucherCustomerRepositoryImpl createVoucherCustomerRepository() {
		return new VoucherCustomerRepositoryImpl(dataSource, new SupplyPointRepositoryImpl(dataSource));
	}

	protected VoucherRepositoryImpl createVoucherRepository() {
		return new VoucherRepositoryImpl(dataSource);
	}

}
