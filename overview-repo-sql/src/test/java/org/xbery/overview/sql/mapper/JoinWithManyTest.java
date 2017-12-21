package org.xbery.overview.sql.mapper;

import org.xbery.overview.Order;
import org.xbery.overview.Overview;
import org.xbery.overview.VoucherTestDb;
import org.xbery.overview.data.CustomerTestData;
import org.xbery.overview.data.SupplyPointTestData;
import org.xbery.overview.domain.Customer;
import org.xbery.overview.domain.CustomerFilter;
import org.xbery.overview.domain.SupplyPoint;
import org.xbery.overview.domain.SupplyPointFilter;
import org.junit.Test;
import org.xbery.overview.sql.repo.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Radek Beran
 */
public class JoinWithManyTest {

    private final DataSource dataSource;
    private final CustomerTestData customerTestData;
    private final SupplyPointTestData supplyPointsTestData;

    public JoinWithManyTest() {
        this.dataSource = new VoucherTestDb().createDataSource();
        this.customerTestData = new CustomerTestData();
        this.supplyPointsTestData = new SupplyPointTestData();
    }

    @Test
    public void findCustomersLeftJoinManySupplyPoints() {
        CustomerRepository repo = createCustomerRepository();
        SupplyPointRepository spRepo = createSupplyPointRepository();

        try {

            // Customers, saved and returned with generated ids
            List<Customer> customersCreated = repo.createAll(createCustomers(), true);
            // Supply points, saved and returned with generated ids
            spRepo.createAll(createSupplyPoints(customersCreated), true);

            // Find all customers joined with supply points
            List<Customer> customersFound = repo.findWithSupplyPoints(Overview.fromOrdering(new Order(CustomerMapper.getInstance().id)));
            assertEquals(customersCreated.size(), customersFound.size());
            for (int i = 0; i < customersFound.size(); i++) {
                Customer customerCreated = customersCreated.get(i);
                Customer customerFound = customersFound.get(i);

                assertEquals(customerCreated.getEmail(), customerFound.getEmail());
                assertEquals(customerCreated.getFirstName(), customerFound.getFirstName());
                assertEquals(customerCreated.getLastName(), customerFound.getLastName());
                assertNotNull("Supply points are set", customerFound.getSupplyPoints());
            }

            assertEquals(2, customersFound.get(0).getSupplyPoints().size());
            assertEquals(3, customersFound.get(1).getSupplyPoints().size());
            assertEquals(1, customersFound.get(2).getSupplyPoints().size());

            SupplyPoint a1 = customersFound.get(0).getSupplyPoints().get(0);
            assertEquals("A1", a1.getCode());
            SupplyPoint a2 = customersFound.get(0).getSupplyPoints().get(1);
            assertEquals("A2", a2.getCode());

            SupplyPoint b1 = customersFound.get(1).getSupplyPoints().get(0);
            assertEquals("B1", b1.getCode());
            SupplyPoint b2 = customersFound.get(1).getSupplyPoints().get(1);
            assertEquals("B2", b2.getCode());
            SupplyPoint b3 = customersFound.get(1).getSupplyPoints().get(2);
            assertEquals("B3", b3.getCode());

            SupplyPoint c1 = customersFound.get(2).getSupplyPoints().get(0);
            assertEquals("C1", c1.getCode());
        } finally {
            // TODO RBe: Clear VoucherTestDb
            // Delete all records after test
            spRepo.deleteByFilter(new SupplyPointFilter());
            repo.deleteByFilter(new CustomerFilter());
        }
    }

    protected CustomerRepository createCustomerRepository() {
        return new CustomerRepositoryImpl(dataSource);
    }

    protected SupplyPointRepository createSupplyPointRepository() {
        return new SupplyPointRepositoryImpl(dataSource);
    }

    protected List<Customer> createCustomers() {
        List<Customer> list = new ArrayList<>();
        list.add(customerTestData.createCustomer("john.smith@gmail.com", "John", "Smith"));
        list.add(customerTestData.createCustomer("vanessa.twiggy@gmail.com", "Vanessa", "Twiggy"));
        list.add(customerTestData.createCustomer("jeremy.scott@gmail.com", "Jeremy", "Scott"));
        return list;
    }

    protected List<SupplyPoint> createSupplyPoints(List<Customer> customersWithIds) {
        List<SupplyPoint> list = new ArrayList<>();

        SupplyPoint a1 = supplyPointsTestData.createSupplyPoint("A1");
        a1.setCustomerId(customersWithIds.get(0).getId());
        list.add(a1);

        SupplyPoint a2 = supplyPointsTestData.createSupplyPoint("A2");
        a2.setCustomerId(customersWithIds.get(0).getId());
        list.add(a2);

        SupplyPoint b1 = supplyPointsTestData.createSupplyPoint("B1");
        b1.setCustomerId(customersWithIds.get(1).getId());
        list.add(b1);

        SupplyPoint b2 = supplyPointsTestData.createSupplyPoint("B2");
        b2.setCustomerId(customersWithIds.get(1).getId());
        list.add(b2);

        SupplyPoint b3 = supplyPointsTestData.createSupplyPoint("B3");
        b3.setCustomerId(customersWithIds.get(1).getId());
        list.add(b3);

        SupplyPoint c1 = supplyPointsTestData.createSupplyPoint("C1");
        c1.setCustomerId(customersWithIds.get(2).getId());
        list.add(c1);

        return list;
    }
}
