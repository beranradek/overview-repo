/*
 * Created on 17. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Order;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link SupplyPointRepository}.
 * @author Radek Beran
 */
public class SupplyPointRepositoryImpl extends AbstractRepository<SupplyPoint, Integer, SupplyPointFilter> implements SupplyPointRepository {
	
	private final DataSource dataSource;

	public SupplyPointRepositoryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public SupplyPointMapper getEntityMapper() {
		return SupplyPointMapper.getInstance();
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public List<SupplyPoint> findByCustomerIds(List<Integer> customerIds) {
		List<Order> ordering = new ArrayList<>();
		ordering.add(new Order(getEntityMapper().code.getNameFull(), false));
		SupplyPointFilter filter = new SupplyPointFilter();
		filter.setCustomerIds(customerIds);
		return findByFilter(filter, ordering);
	}

}
