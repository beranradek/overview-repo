package cz.etn.overview.repo;

/*
 * Created on 9. 2. 2017
 *
 * Copyright (c) 2017 Etnetera, a.s. All rights reserved.
 * Intended for internal use only.
 * http://www.etnetera.cz
 */

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import cz.etn.overview.Overview;
import cz.etn.overview.common.Pair;
import cz.etn.overview.domain.SendingState;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.domain.VoucherCustomer;
import cz.etn.overview.domain.VoucherCustomerFilter;
import cz.etn.overview.funs.CollectionFuns;
import cz.etn.overview.mapper.EntityMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link VoucherCustomerRepository}.
 * @author Radek Beran
 */
public class VoucherCustomerRepositoryImpl extends AbstractRepository<VoucherCustomer, Integer, VoucherCustomerFilter> implements VoucherCustomerRepository {

    private final DataSource dataSource;

    private final SupplyPointRepository supplyPointDao;

    public VoucherCustomerRepositoryImpl(DataSource dataSource, SupplyPointRepository supplyPointDao) {
        this.dataSource = dataSource;
        this.supplyPointDao = supplyPointDao;
    }

    @Override
    public VoucherCustomerMapper getEntityMapper() {
        return VoucherCustomerMapper.getInstance();
    }

    @Override
    public VoucherCustomer create(VoucherCustomer entity, boolean autogenerateKey) {
        entity.setEmailSendingState(SendingState.READY);
        return super.create(entity, autogenerateKey);
    }

    // This is overriden, so the related voucher and supply points are also fetched
    @Override
    public Optional<VoucherCustomer> findById(Integer id) {
        VoucherCustomerFilter filter = new VoucherCustomerFilter();
        filter.setId(id);
        return CollectionFuns.headOpt(findByFilter(filter));
    }

    /**
     * Loads customers including joined voucher and supply points data.
     */
    @Override
    public List<VoucherCustomer> findByOverview(Overview<VoucherCustomerFilter> overview) {
        Objects.requireNonNull(overview, "overview should be specified");
        // First load customers joined with (optional) vouchers
        // Resulting records are suitable for directly applying pagination settings
        List<VoucherCustomer> customers = findByOverview(overview, getCustomerLeftJoinVoucherMapper());

        // Lazy loading of related supply points using one additional query (if they would be joined with customers in one query, it would break pagination limit)
        List<Integer> customerIds = customers.stream().map(c -> c.getId()).collect(Collectors.toList());
        List<SupplyPoint> supplyPoints = supplyPointDao.findByCustomerIds(customerIds);

        // Append supply points to customers
        for (VoucherCustomer customer : customers) {
            Iterable<SupplyPoint> supplyPointsOfCustomer = Iterables.filter(supplyPoints, sp -> customer.getId().equals(sp.getCustomerId()));
            customer.setSupplyPoints(Lists.newArrayList(supplyPointsOfCustomer));
        }

        return customers;
    }

    /**
     * Computes customers aggregation value including applied conditions on joined customer and voucher data.
     */
    @Override
    public <R> R aggByFilter(AggType aggType, Class<R> resultClass, String attrName, VoucherCustomerFilter filter) {
        return super.aggByFilter(aggType, resultClass, attrName, filter, getCustomerLeftJoinVoucherMapper());
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    protected EntityMapper<VoucherCustomer, VoucherCustomerFilter> getCustomerLeftJoinVoucherMapper() {
        return getEntityMapper().leftJoin(getVoucherMapper(),
            Condition.eqAttributes(getEntityMapper().id, getVoucherMapper().reserved_by), // ON condition
            (customer, voucher) -> { customer.setVoucher(voucher); return customer; }, // joined entity composition
            filter -> new Pair<>(filter, filter) // filter decomposition to first and second entity filters
        );
    }

    private VoucherMapper getVoucherMapper() {
        return VoucherMapper.getInstance();
    }
}
