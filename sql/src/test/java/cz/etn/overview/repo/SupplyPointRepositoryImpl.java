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

import cz.etn.overview.Order;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.SupplyPointFilter;
import cz.etn.overview.sql.repo.AbstractSqlRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link SupplyPointRepository}.
 * @author Radek Beran
 */
public class SupplyPointRepositoryImpl extends AbstractSqlRepository<SupplyPoint, Integer, SupplyPointFilter> implements SupplyPointRepository {
	
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
