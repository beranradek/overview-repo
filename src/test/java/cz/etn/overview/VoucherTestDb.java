/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * In-memory test database for vouchers.
 * @author Radek Beran
 */
public class VoucherTestDb {

	/**
	 * Creates data source to in-memory H2 database. 
	 * @return
	 */
	public DataSource createDataSource() {
		JdbcDataSource ds = new JdbcDataSource();
		// H2, by default, drops your in memory database if there are no connections to it anymore.
		// You probably don't want this to happen (e.g. you have multiple queries with different connections).
		// To prevent this add DB_CLOSE_DELAY=-1 to the url (use a semicolon as a separator). 
		
		// voucher-create.sql is also intended for production database
		ds.setURL("jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:/" + VoucherTestDb.class.getPackage().getName().replace('.', '/') + "/voucher-create.sql'");
		ds.setUser("sa");
		ds.setPassword("sa");
		return ds;
	}
}
