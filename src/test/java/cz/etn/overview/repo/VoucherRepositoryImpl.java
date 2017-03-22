/*
 * Created on 8. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Filter;
import cz.etn.overview.domain.Voucher;
import cz.etn.overview.mapper.EntityMapper;

import javax.sql.DataSource;


/**
 * Default implementation of {@link VoucherRepository}. 
 * @author Radek Beran
 */
public class VoucherRepositoryImpl extends AbstractRepository<Voucher, String, Filter> implements VoucherRepository {
	
	private final DataSource dataSource;

	public VoucherRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected EntityMapper<Voucher> getEntityMapper() {
		return VoucherMapper.INSTANCE;
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

}
