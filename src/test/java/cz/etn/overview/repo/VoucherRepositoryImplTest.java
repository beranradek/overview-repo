/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import cz.etn.overview.VoucherTestData;
import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.common.Pair;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.Attribute;
import org.apache.commons.lang3.builder.EqualsBuilder;
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

	@Test
	public void createFindDeleteVoucher() {
		Voucher voucher = testData.newVoucher("ABCD");

		Voucher voucherCreated = repo.create(voucher, false);
		assertTrue("Created voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, voucherCreated));
		
		assertEquals(voucher.getCode(), voucher.getId());
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getId());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " equals voucher to store " + voucher, EqualsBuilder.reflectionEquals(voucher, foundVoucherOpt.get()));
		
		repo.delete(voucher.getId());
		Optional<Voucher> foundVoucherAfterDeleteOpt = repo.findById(voucher.getId());
		assertFalse("Voucher should not be present in database after deletion", foundVoucherAfterDeleteOpt.isPresent());
	}
	
	@Test
	public void updateVoucher() {
		Voucher voucher = testData.newVoucher("QWERTY");

		Voucher voucherCreated = repo.create(voucher, false);
		
		Instant redemptionTime = Instant.now().plus(Duration.ofDays(2));
		Voucher voucherToUpdate = voucherCreated;
		voucherToUpdate.setRedeemedBy("customerXY");
		voucherToUpdate.setSoldBy("sellerXY");
		voucherToUpdate.setRedemptionTime(redemptionTime);
		Optional<Voucher> voucherUpdatedOpt = repo.update(voucherToUpdate);
		assertTrue("Updated voucher " + voucherUpdatedOpt.get() + " equals voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(voucherUpdatedOpt.get(), voucherToUpdate));
		
		Optional<Voucher> foundVoucherOpt = repo.findById(voucher.getId());
		assertTrue("Found voucher " + foundVoucherOpt.get() + " is equal to voucher to update " + voucherToUpdate, EqualsBuilder.reflectionEquals(foundVoucherOpt.get(), voucherToUpdate));
	}

	@Test
	public void updateSelectedAttributes() {
		Voucher voucher = testData.newVoucher("HGTDFKL");
		VoucherMapper mapper = VoucherMapper.getInstance();
		BigDecimal newDiscountPrice = BigDecimal.valueOf(200000, 2);
		String newInvoiceNote = "Updated invoice note";

		repo.create(voucher, false);
		int updatedCnt = repo.update(voucher.getId(), Arrays.asList(new Pair[] {
			new Pair<>(mapper.discount_price, newDiscountPrice),
			new Pair<>(mapper.invoice_note, newInvoiceNote)
		}));

		assertEquals(1, updatedCnt);
		Voucher updatedVoucher = repo.findById(voucher.getId()).get();
		assertEquals(newDiscountPrice, updatedVoucher.getDiscountPrice());
		assertEquals(newInvoiceNote, updatedVoucher.getInvoiceNote());
	}

}
