/*
 * Created on 17. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

package cz.etn.overview.repo;


import com.google.common.collect.Lists;
import cz.etn.overview.Order;
import cz.etn.overview.Overview;
import cz.etn.overview.common.Funs;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;
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
		ordering.add(new Order(getEntityMapper().getDataSet() + "." + SupplyPointMapper.code.getName(), false));
		SupplyPointFilter filter = new SupplyPointFilter();
		filter.setCustomerIds(customerIds);
		return findByOverview(new Overview<>(filter, ordering, null));
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
				String attrName = dataSet + "." + SupplyPointMapper.id.getName();
				conditions.add(new FilterCondition(attrName + "=?", Lists.newArrayList(filter.getId())));
			}
			if (filter.getCustomerId() != null) {
				String attrName = dataSet + "." + SupplyPointMapper.customer_id.getName();
				conditions.add(new FilterCondition(attrName + "=?", Lists.newArrayList(filter.getCustomerId())));
			}
			if (filter.getCustomerIds() != null) {
				if (!filter.getCustomerIds().isEmpty()) {
					String attrName = dataSet + "." + SupplyPointMapper.customer_id.getName();
					conditions.add(new FilterCondition(attrName + " IN (" + Funs.mkString(filter.getCustomerIds(), customerId -> "" + customerId, ", ") + ")", Lists.newArrayList()));
				} else {
					// empty customer ids
					conditions.add(new FilterCondition("1=0", Lists.newArrayList()));
				}
			}
		}
		return conditions;
	}

}
