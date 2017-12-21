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
package org.xbery.overview;

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
