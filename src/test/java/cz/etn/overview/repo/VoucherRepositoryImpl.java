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
import java.util.Optional;


/**
 * Default implementation of {@link VoucherRepository}. 
 * @author Radek Beran
 */
public class VoucherRepositoryImpl extends AbstractRepositoryImpl<Voucher, String, Filter> implements VoucherRepository {
	
	private final DataSource dataSource;

	public VoucherRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Optional<Voucher> findByCode(String code) {
		return findByAttributeValue(VoucherMapper.code.getAttributeName(), Voucher.normalizedVoucherCode(code));
	}
	
	@Override
	public EntityMapper<Voucher> getEntityMapper() {
		return VoucherMapper.INSTANCE;
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

}
