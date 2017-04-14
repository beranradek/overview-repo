/*
 * Created on 17. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;

import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.mapper.EntityMapper;

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
	protected EntityMapper<SupplyPoint> getEntityMapper() {
		return SupplyPointMapper.INSTANCE;
	}

	@Override
	protected DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public List<SupplyPoint> findByCustomerIds(List<Integer> customerIds) {
		List<Order> ordering = new ArrayList<>();
		ordering.add(new Order(SupplyPointMapper.code.getNameFull(), false));
		SupplyPointFilter filter = new SupplyPointFilter();
		filter.setCustomerIds(customerIds);
		return findByFilter(filter, ordering);
	}
	
	@Override
	protected SupplyPoint entityUpdatedWithId(SupplyPoint entity, Integer id) {
		entity.setId(id);
		return entity;
	}
	
	@Override
	protected List<FilterCondition> composeFilterConditions(SupplyPointFilter filter) {
		List<FilterCondition> conditions = new ArrayList<>(); 
		String dataSet = getEntityMapper().getDataSet();
		if (filter != null) {
			if (filter.getId() != null) {
				conditions.add(FilterCondition.eq(SupplyPointMapper.id, filter.getId()));
			}
			if (filter.getCustomerId() != null) {
				conditions.add(FilterCondition.eq(SupplyPointMapper.customer_id, filter.getCustomerId()));
			}
			if (filter.getCustomerIds() != null) {
				conditions.add(FilterCondition.in(SupplyPointMapper.customer_id, CollectionFuns.toObjectList(filter.getCustomerIds())));
			}
		}
		return conditions;
	}

}
