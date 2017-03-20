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
import cz.etn.overview.common.Funs;
import cz.etn.overview.domain.*;
import cz.etn.overview.mapper.AttributeSource;
import cz.etn.overview.mapper.EntityMapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Default implementation of {@link VoucherCustomerRepository}.
 * @author Radek Beran
 */
public class VoucherCustomerRepositoryImpl extends AbstractRepositoryImpl<VoucherCustomer, Integer, VoucherCustomerFilter> implements VoucherCustomerRepository {

    private final DataSource dataSource;

    private final SupplyPointRepository supplyPointDao;

    public VoucherCustomerRepositoryImpl(DataSource dataSource, SupplyPointRepository supplyPointDao) {
        this.dataSource = dataSource;
        this.supplyPointDao = supplyPointDao;
    }

    @Override
    protected EntityMapper<VoucherCustomer> getEntityMapper() {
        return VoucherCustomerMapper.INSTANCE;
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
        return Funs.headOpt(findByOverview(new Overview<>(filter, null, null)));
    }

    /**
     * Loads customers including joined voucher and supply points data.
     */
    @Override
    public List<VoucherCustomer> findByOverview(Overview<VoucherCustomerFilter> overview) {
        Objects.requireNonNull(overview, "overview should be specified");
        List<VoucherCustomer> customers = withNewConnection(conn -> {
            // First load customers joined with (optional) vouchers
            // Resulting records are suitable for directly applying pagination settings
            Pair<List<String>, String> attrsAndFrom = joinedSelectionAndFrom();
            return findByOverviewInternal(overview, attrsAndFrom.getLeft(), attrsAndFrom.getRight(), attributeSource -> customerFromAttributeSource(attributeSource));
        });

        // Lazy loading of related supply points using one additional query (if they would be joined with customers in one query, it would break pagination limit)
        List<Integer> customerIds = Lists.newArrayList(Iterables.transform(customers, c -> c.getId()));
        List<SupplyPoint> supplyPoints = supplyPointDao.findByCustomerIds(customerIds);

        // Append supply points to customers
        for (VoucherCustomer customer : customers) {
            Iterable<SupplyPoint> supplyPointsOfCustomer = Iterables.filter(supplyPoints, sp -> sp.getCustomerId() != null && sp.getCustomerId().equals(customer.getId()));
            customer.setSupplyPoints(Lists.newArrayList(supplyPointsOfCustomer));
        }

        return customers;
    }

    /**
     * Computes customers count including applied conditions on joined voucher and supply points data.
     */
    @Override
    public int countByOverview(Overview<VoucherCustomerFilter> overview) {
        Pair<List<String>, String> attributesAndFrom = joinedSelectionAndFrom();
        String customerIdAttribute = getEntityMapper().getTableName() + "." + VoucherCustomerMapper.id.getName();
        return countByOverviewInternal(overview, "COUNT(" + customerIdAttribute + ")", attributesAndFrom.getRight());
    }

    /**
     * Returns select clause and from clause for joined customer with voucher data.
     * @return
     */
    protected Pair<List<String>, String> joinedSelectionAndFrom() {
        String customerTable = getEntityMapper().getTableName();
        List<String> customerAttrs = getEntityMapper().getAttributeNamesWithPrefix(customerTable, getEntityMapper().getAliasPrefix());
        String voucherTable = getVoucherMapper().getTableName();
        List<String> voucherAttrs = getVoucherMapper().getAttributeNamesWithPrefix(voucherTable, getVoucherMapper().getAliasPrefix());

        List<String> dbAttributesJoined = new ArrayList<>();
        dbAttributesJoined.addAll(customerAttrs);
        dbAttributesJoined.addAll(voucherAttrs);

        String fromJoined = customerTable +
            " LEFT JOIN " + voucherTable + " ON (" + customerTable + "." + VoucherCustomerMapper.id + "=" + voucherTable + "." + VoucherMapper.reserved_by + ")";

        return Pair.of(dbAttributesJoined, fromJoined);
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public List<FilterCondition> composeFilterConditions(VoucherCustomerFilter filter) {
        List<FilterCondition> conditions = new ArrayList<>();
        String tableName = getEntityMapper().getTableName();
        if (filter != null) {
//            if (filter.getId() != null) {
//                String attrName = tableName + "." + VoucherCustomerMapper.id.getName();
//                conditions.add(new FilterCondition(attrName + "=?", Lists.newArrayList(filter.getId())));
//            }
//            if (filter.getImportFileName() != null) {
//                String attrName = tableName + "." + VoucherCustomerMapper.import_file_name.getName();
//                conditions.add(new FilterCondition(attrName + "=?", Lists.newArrayList(filter.getImportFileName())));
//            }
//            if (filter.getSoldBy() != null) {
//                String attrName = VoucherMapper.INSTANCE.getTableName() + "." + VoucherMapper.sold_by.getName();
//                conditions.add(new FilterCondition(attrName + "=?", Lists.newArrayList(filter.getSoldBy())));
//            }
//            if (filter.getCustomerIds() != null) {
//                if (!filter.getCustomerIds().isEmpty()) {
//                    String attrName = tableName + "." + VoucherCustomerMapper.id.getName();
//                    conditions.add(new FilterCondition(attrName + " IN (" + Funs.mkString(filter.getCustomerIds(), customerId -> "" + customerId, ", ") + ")", Lists.newArrayList()));
//                } else {
//                    // empty customer ids
//                    conditions.add(new FilterCondition("1=0", Lists.newArrayList()));
//                }
//            }
//            if (filter.getLatestInvoiceOfSeller() != null && filter.getLatestInvoiceOfSeller().booleanValue() && filter.getSoldBy() != null) {
//                String invoiceTimeAttrName = VoucherMapper.INSTANCE.getTableName() + "." + VoucherMapper.invoice_time.getName();
//                String soldByAttrName = VoucherMapper.INSTANCE.getTableName() + "." + VoucherMapper.sold_by.getName();
//                conditions.add(new FilterCondition(invoiceTimeAttrName + " IS NOT NULL AND " + invoiceTimeAttrName + "=(SELECT MAX(" + invoiceTimeAttrName + ") FROM " + VoucherMapper.INSTANCE.getTableName() + " WHERE " + soldByAttrName + "=" + filter.getSoldBy() + ")", Lists.newArrayList()));
//            }
        }
        return conditions;
    }

    private VoucherCustomer customerFromAttributeSource(AttributeSource attributeSource) {
        VoucherCustomer customer = getEntityMapper().buildEntity(attributeSource, getEntityMapper().getAliasPrefix());
        Voucher voucher = getVoucherMapper().buildEntity(attributeSource, getVoucherMapper().getAliasPrefix());

        // Join the object representation of customer with voucher
        if (voucher.getId() != null && !voucher.getId().isEmpty()) {
            customer.setVoucher(voucher);
        }
        return customer;
    }

    private EntityMapper<Voucher> getVoucherMapper() {
        return VoucherMapper.INSTANCE;
    }
}
