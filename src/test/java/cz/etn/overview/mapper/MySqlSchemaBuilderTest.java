package cz.etn.overview.mapper;

import cz.etn.overview.repo.VoucherMapper;
import org.junit.Test;

/**
 * @author Radek Beran
 */
public class MySqlSchemaBuilderTest {

    @Test
    public void composeCreateTableSQL() {
        VoucherMapper mapper = VoucherMapper.getInstance().getInstance();
        System.out.println(new MySqlSchemaBuilder().composeCreateTableSQL(mapper.getDataSet(), mapper.getAttributes()));
    }

}
