package org.xbery.overview.sql.mapper;

import org.xbery.overview.sql.repo.VoucherMapper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Radek Beran
 */
public class MySqlSchemaBuilderTest {

    @Test
    public void composeCreateTableSQL() {
        VoucherMapper mapper = VoucherMapper.getInstance().getInstance();
        String sql = new MySqlSchemaBuilder().composeCreateTableSQL(mapper.getTableName(), mapper.getAttributes());
        System.out.println(sql);
        assertTrue("Generated CREATE TABLE command is not as expected", sql.contains("CREATE TABLE IF NOT EXISTS `" + mapper.getTableName() + "`"));
        assertTrue("Generated CREATE TABLE command does not contain code attribute", sql.contains("`code` VARCHAR(20) NOT NULL"));
    }

}
