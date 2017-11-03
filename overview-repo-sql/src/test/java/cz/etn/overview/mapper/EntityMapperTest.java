package cz.etn.overview.mapper;

import cz.etn.overview.sql.repo.VoucherMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Radek Beran
 */
public class EntityMapperTest {
    @Test
    public void getTableNameWithDb() {
        assertEquals("db_en.voucher", createEntityMapperWithDb().getTableNameWithDb());
        assertEquals("voucher", createEntityMapper().getTableNameWithDb());
    }

    public VoucherMapper createEntityMapper() {
        return VoucherMapper.getInstance();
    }

    public VoucherMapper createEntityMapperWithDb() {
        return new VoucherMapperWithDb();
    }

    static class VoucherMapperWithDb extends VoucherMapper {
        @Override
        public String getDbName() {
            return "db_en";
        }
    }
}
