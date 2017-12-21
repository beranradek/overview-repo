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


import org.xbery.overview.VoucherTestDb;
import org.xbery.overview.common.Pair;
import org.xbery.overview.data.VoucherTestData;
import org.xbery.overview.domain.Voucher;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests for {@link VoucherRepositoryImpl}.
 * @author Radek Beran
 */
public class VoucherRepositoryImplTest {
	
	private final DataSource dataSource;
	private final VoucherTestData testData;
	private final VoucherRepository repo;
	
	public VoucherRepositoryImplTest() {
		VoucherTestDb testDb = new VoucherTestDb();
		this.dataSource = testDb.createDataSource();
		this.testData = new VoucherTestData();
		this.repo = new VoucherRepositoryImpl(dataSource);
	}

	@After
	public void runAfter() {
		// TODO RBe: Clear VoucherTestDb
		// Delete all records after test
		repo.deleteByFilter(new Object());
	}

	@Test
	public void create() {
		Voucher voucher = testData.createVoucher("ABCD");

		Voucher voucherCreated = repo.create(voucher, false);
		assertTrue("Created voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, voucherCreated));
	}

	@Test
	public void createFindDelete() {
		Voucher voucher = testData.createVoucher("EFGH");

		Voucher voucherCreated = repo.create(voucher, false);
		assertTrue("Created voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, voucherCreated));
		
		assertEquals(voucher.getCode(), voucher.getCode());
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getCode());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " equals voucher to store " + voucher, EqualsBuilder.reflectionEquals(voucher, foundVoucherOpt.get()));
		
		repo.delete(voucher.getCode());
		Optional<Voucher> foundVoucherAfterDeleteOpt = repo.findById(voucher.getCode());
		assertFalse("Voucher should not be present in database after deletion", foundVoucherAfterDeleteOpt.isPresent());
	}
	
	@Test
	public void update() {
		Voucher voucher = testData.createVoucher("QWERTY");

		Voucher voucherCreated = repo.create(voucher, false);
		
		Instant redemptionTime = Instant.now().plus(Duration.ofDays(2));
		Voucher voucherToUpdate = voucherCreated;
		voucherToUpdate.setRedeemedBy("customerXY");
		voucherToUpdate.setSoldBy("sellerXY");
		voucherToUpdate.setRedemptionTime(redemptionTime);
		Optional<Voucher> voucherUpdatedOpt = repo.update(voucherToUpdate);
		assertTrue("Updated voucher " + voucherUpdatedOpt.get() + " equals voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(voucherUpdatedOpt.get(), voucherToUpdate));
		
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getCode());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " is equal to voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(foundVoucherOpt.get(), voucherToUpdate));
	}

	@Test
	public void updateSelectedAttributes() {
		Voucher voucher = testData.createVoucher("HGTDFKL");
		VoucherMapper mapper = VoucherMapper.getInstance();

		repo.create(voucher, false);

		Voucher voucherToUpdate = SerializationUtils.clone(voucher);
		voucherToUpdate.setInvalidationNote("Something changed but not stored");
		BigDecimal newDiscountPrice = BigDecimal.valueOf(200000, 2);
		String newInvoiceNote = "Updated invoice note";
		int updatedCnt = repo.update(voucherToUpdate.getCode(), Arrays.asList(new Pair[] {
			new Pair<>(mapper.discount_price, newDiscountPrice),
			new Pair<>(mapper.invoice_note, newInvoiceNote)
		}));

		assertEquals(1, updatedCnt);
		Voucher updatedVoucher = repo.findById(voucher.getCode()).get();
		assertEquals(newDiscountPrice, updatedVoucher.getDiscountPrice());
		assertEquals(newInvoiceNote, updatedVoucher.getInvoiceNote());
		assertEquals(voucher.getInvalidationNote(), updatedVoucher.getInvalidationNote());
	}

	@Test
	public void updateWithFunction() {
		Voucher voucher = testData.createVoucher("IJKLM");
		VoucherMapper mapper = VoucherMapper.getInstance();

		repo.create(voucher, false);

		Voucher voucherToUpdate = SerializationUtils.clone(voucher);
		voucherToUpdate.setInvalidationNote("Something changed but not stored");

		BigDecimal newDiscountPrice = BigDecimal.valueOf(200000, 2);
		String newInvoiceNote = "Updated invoice note";

		Optional<Voucher> updatedVoucherOpt = repo.update(voucherToUpdate.getCode(), v -> {
			v.setDiscountPrice(newDiscountPrice);
			v.setInvoiceNote(newInvoiceNote);
			return v;
		});
		assertTrue("Updated voucher is returned", updatedVoucherOpt.isPresent());
		assertEquals(voucher.getCode(), updatedVoucherOpt.get().getCode());
		assertEquals(newDiscountPrice, updatedVoucherOpt.get().getDiscountPrice());
		assertEquals(newInvoiceNote, updatedVoucherOpt.get().getInvoiceNote());
		assertEquals(voucher.getInvalidationNote(), updatedVoucherOpt.get().getInvalidationNote());
	}

	@Test
	public void delete() {
		String code = "HCHKR";
		Voucher voucher = testData.createVoucher(code);
		repo.create(voucher, false);
		assertTrue("Voucher is available in DB", repo.findById(code).isPresent());
		repo.delete(code);
		assertFalse("After deletion, voucher is not available in DB", repo.findById(code).isPresent());
	}

	@Test
	public void findById() {
		String code = "ASDFG";
		Voucher voucher = testData.createVoucher(code);
		repo.create(voucher, false);
		Optional<Voucher> foundVoucherOpt = repo.findById(code);
		assertTrue("Voucher is available in DB", foundVoucherOpt.isPresent());
		assertTrue("Found voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, foundVoucherOpt.get()));
	}

}
