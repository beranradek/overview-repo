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
package cz.etn.overview.repo;

import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.sql.repo.AbstractSqlRepository;

import javax.sql.DataSource;


/**
 * Default implementation of {@link VoucherRepository}. 
 * @author Radek Beran
 */
public class VoucherRepositoryImpl extends AbstractSqlRepository<Voucher, String, Object> implements VoucherRepository {
	
	private final DataSource dataSource;

	public VoucherRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public EntityMapper<Voucher, Object> getEntityMapper() {
		return VoucherMapper.getInstance();
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

}
