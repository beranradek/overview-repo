package cz.etn.overview.mapper;

import cz.etn.overview.repo.VoucherMapper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Radek Beran
 */
public class MySqlSchemaBuilderTest {

    @Test
    public void composeCreateTableSQL() {
        VoucherMapper mapper = VoucherMapper.getInstance().getInstance();
        String sql = new MySqlSchemaBuilder().composeCreateTableSQL(mapper.getDataSet(), mapper.getAttributes());
        System.out.println(sql);
        assertTrue("Generated CREATE TABLE command is not as expected", sql.contains("CREATE TABLE IF NOT EXISTS `" + mapper.getDataSet() + "`"));
        assertTrue("Generated CREATE TABLE command does not contain code attribute", sql.contains("`code` VARCHAR(20) NOT NULL"));
    }

}
