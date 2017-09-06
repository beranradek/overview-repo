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
import cz.etn.overview.common.Pair;
import cz.etn.overview.domain.*;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.filter.Condition;
import cz.etn.overview.mapper.EntityMapper;
import cz.etn.overview.repo.AggType;
import cz.etn.overview.repo.Conditions;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link CustomerRepository}.
 * @author Radek Beran
 */
public class CustomerRepositoryImpl extends AbstractSqlRepository<Customer, Integer, CustomerFilter> implements CustomerRepository {

    private final DataSource dataSource;

    private final SupplyPointRepository supplyPointDao;

    public CustomerRepositoryImpl(DataSource dataSource, SupplyPointRepository supplyPointDao) {
        this.dataSource = dataSource;
        this.supplyPointDao = supplyPointDao;
    }

    @Override
    public CustomerMapper getEntityMapper() {
        return CustomerMapper.getInstance();
    }

    // This is overriden, so the related voucher and supply points are also fetched
    @Override
    public Optional<Customer> findById(Integer id) {
        CustomerFilter filter = new CustomerFilter();
        filter.setId(id);
        return CollectionFuns.headOpt(findByFilter(filter));
    }

    /**
     * Loads customers including joined voucher and supply points data.
     */
    @Override
    public List<Customer> findByOverview(Overview<CustomerFilter> overview) {
        Objects.requireNonNull(overview, "overview should be specified");
        return findByOverview(overview, getCustomerLeftJoinVoucherLeftJoinSupplyPointsMapper());
    }

    /**
     * Computes customers aggregation value including applied conditions on joined customer and voucher data.
     */
    @Override
    public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, CustomerFilter filter) {
        return super.aggByFilter(aggType, resultClass, attrName, filter, getCustomerLeftJoinVoucherMapper());
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    protected EntityMapper<Customer, CustomerFilter> getCustomerLeftJoinVoucherMapper() {
        return getEntityMapper().leftJoin(getVoucherMapper(),
            Conditions.eqAttributes(getEntityMapper().id, getVoucherMapper().reserved_by), // ON condition
            (customer, voucher) -> { customer.setVoucher(voucher); return customer; }, // joined entity composition
            filter -> new Pair<>(filter, filter) // filter decomposition to first and second entity filters
        );
    }

    protected EntityMapper<Customer, CustomerFilter> getCustomerLeftJoinSupplyPointsMapper() {
        return getEntityMapper().joinWithMany(getSupplyPointMapper(),
            Conditions.eqAttributes(getEntityMapper().id, getSupplyPointMapper().customer_id),
            new ArrayList<Condition>(), // additional ON condition
            (customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; }, // joined entity composition with many records
            filter -> new Pair<>(filter, new SupplyPointFilter()), // filter decomposition to first and second entity filters
            ordering -> new Pair<>(ordering, CollectionFuns.empty())
        );
    }

    protected EntityMapper<Customer, CustomerFilter> getCustomerLeftJoinVoucherLeftJoinSupplyPointsMapper() {
        return getCustomerLeftJoinVoucherMapper().joinWithMany(getSupplyPointMapper(),
            Conditions.eqAttributes(getEntityMapper().id, getSupplyPointMapper().customer_id),
            new ArrayList<Condition>(), // additional ON condition
            (customer, supplyPoints) -> { customer.setSupplyPoints(supplyPoints); return customer; }, // joined entity composition with many records
            filter -> new Pair<>(filter, new SupplyPointFilter()), // filter decomposition to first and second entity filters
            ordering -> new Pair<>(ordering, CollectionFuns.empty())
        );
    }

    protected VoucherMapper getVoucherMapper() {
        return VoucherMapper.getInstance();
    }

    protected SupplyPointMapper getSupplyPointMapper() {
        return SupplyPointMapper.getInstance();
    }
}
