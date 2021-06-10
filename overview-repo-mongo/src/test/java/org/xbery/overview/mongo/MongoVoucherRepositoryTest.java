package org.xbery.overview.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;
import org.xbery.overview.VoucherTestData;
import org.xbery.overview.common.Pair;
import org.xbery.overview.domain.Voucher;
import org.xbery.overview.repo.VoucherMapper;
import org.xbery.overview.repo.VoucherRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Radek Beran
 */
public class MongoVoucherRepositoryTest {

    private static final String DB_NAME = "overrepotest_" + UUID.randomUUID().toString();

    private final VoucherTestData testData;
    private final MongoDatabase db;
    private final VoucherRepository repo;

    public MongoVoucherRepositoryTest() {
        this.db = new Fongo("mongo server 1").getDatabase(DB_NAME); // database instance and a database from it
        this.testData = new VoucherTestData();
        this.repo = new MongoVoucherRepository(db);
    }

    @Test
    public void create() {
        Voucher voucher = testData.newVoucher("ABCD");

        Voucher voucherCreated = repo.create(voucher, false);
        assertTrue("Created voucher equals voucher to store", EqualsBuilder.reflectionEquals(voucher, voucherCreated));
    }

    @Test
    public void update() {
        Voucher voucher = testData.newVoucher("ABCDEF");

        Voucher voucherCreated = repo.create(voucher, false);
        Voucher voucherToUpdate = voucherCreated;
        voucherToUpdate.setReservedBy("cust01");
        voucherToUpdate.setDiscountPrice(BigDecimal.valueOf(200000, 2));

        Optional<Voucher> voucherUpdatedOpt = repo.update(voucherToUpdate);
        voucherToUpdate.setCreationTime(voucherUpdatedOpt.get().getCreationTime()); // so the entities are now equal (precision differs)
        voucherToUpdate.setValidFrom(voucherUpdatedOpt.get().getValidFrom()); // so the entities are now equal (precision differs)
        voucherToUpdate.setRedemptionTime(voucherUpdatedOpt.get().getRedemptionTime()); // so the entities are now equal (precision differs)
        assertTrue("Updated voucher equals voucher to update", EqualsBuilder.reflectionEquals(voucherUpdatedOpt.get(), voucherToUpdate));
    }

    @Test
    public void updateSelectedAttributes() {
        Voucher voucher = testData.newVoucher("HGTDFKL");
        VoucherMapper mapper = VoucherMapper.getInstance();
        BigDecimal newDiscountPrice = BigDecimal.valueOf(200000, 2);
        String newInvoiceNote = "Updated invoice note";

        Voucher createdVoucher = repo.create(voucher, false);
        int updatedCnt = repo.update(createdVoucher.getCode(), Arrays.asList(new Pair[] {
            new Pair<>(mapper.discount_price, newDiscountPrice),
            new Pair<>(mapper.invoice_note, newInvoiceNote)
        }));

        assertEquals(1, updatedCnt);
        Voucher updatedVoucher = repo.findById(voucher.getCode()).get();
        assertEquals(newDiscountPrice, updatedVoucher.getDiscountPrice());
        assertEquals(newInvoiceNote, updatedVoucher.getInvoiceNote());
    }
}
