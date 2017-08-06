package cz.etn.overview.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;
import cz.etn.overview.VoucherTestData;
import cz.etn.overview.common.Pair;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.repo.VoucherMapper;
import cz.etn.overview.repo.VoucherRepository;
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
