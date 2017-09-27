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
package cz.etn.overview.sql.repo;

import cz.etn.overview.Overview;
import cz.etn.overview.domain.Customer;
import cz.etn.overview.domain.CustomerFilter;
import cz.etn.overview.domain.SupplyPointFilter;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.mapper.Joins;

import javax.sql.DataSource;
import java.util.List;

/**
 * Default implementation of {@link CustomerRepository}.
 * @author Radek Beran
 */
public class CustomerRepositoryImpl extends AbstractSqlRepository<Customer, Integer, CustomerFilter> implements CustomerRepository {

    private final DataSource dataSource;

    final EntityMapper<Customer, CustomerFilter> joinSupplyPointsMapper = getEntityMapper().leftJoin(getSupplyPointMapper(), Customer.class, CustomerFilter.class, Integer.class)
        .on(getEntityMapper().id, getSupplyPointMapper().customer_id)
        .composeEntityWithMany((customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; })
        .decomposeFilter(Joins.filterForLeftSideWithRightFilter(new SupplyPointFilter()))
        .build();

    final EntityMapper<Customer, CustomerFilter> joinVoucherMapper = getEntityMapper().leftJoin(getVoucherMapper(), Customer.class, CustomerFilter.class, String.class)
        .on(getEntityMapper().id.as(String.class, a -> a.toString(), a -> Integer.parseInt(a)), getVoucherMapper().reserved_by)
        .composeEntity((customer, voucher) -> { customer.setVoucher(voucher); return customer; })
        .decomposeFilter(Joins.filterForLeftSide())
        .build();

    final EntityMapper<Customer, CustomerFilter> joinVoucherJoinSupplyPointsMapper = joinVoucherMapper.leftJoin(getSupplyPointMapper(), Customer.class, CustomerFilter.class, Integer.class)
        .on(getEntityMapper().id, getSupplyPointMapper().customer_id)
        .composeEntityWithMany((customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; })
        .decomposeFilter(Joins.filterForLeftSideWithRightFilter(new SupplyPointFilter()))
        .build();

    public CustomerRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CustomerMapper getEntityMapper() {
        return CustomerMapper.getInstance();
    }

    @Override
    public List<Customer> findWithVoucherAndSupplyPoints(Overview<CustomerFilter> overview) {
        return findByOverview(overview, joinVoucherJoinSupplyPointsMapper);
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    protected VoucherMapper getVoucherMapper() {
        return VoucherMapper.getInstance();
    }

    protected SupplyPointMapper getSupplyPointMapper() {
        return SupplyPointMapper.getInstance();
    }
}
