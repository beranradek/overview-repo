package cz.etn.overview.sql.mapper;

import cz.etn.overview.Overview;
import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.common.funs.CollectionFuns;
import cz.etn.overview.data.CustomerTestData;
import cz.etn.overview.data.SupplyPointTestData;
import cz.etn.overview.domain.Customer;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.sql.repo.CustomerRepository;
import cz.etn.overview.sql.repo.CustomerRepositoryImpl;
import cz.etn.overview.sql.repo.SupplyPointRepository;
import cz.etn.overview.sql.repo.SupplyPointRepositoryImpl;
import org.junit.Test;

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
        List<Customer> customers = createCustomersWithSupplyPoints();
        saveCustomersWithSupplyPoints(repo, spRepo, customers);

        // Find all customers joined with supply points
        List<Customer> foundCustomers = repo.findWithSupplyPoints(Overview.empty());
        assertEquals(customers.size(), foundCustomers.size());
        for (int i = 0; i < foundCustomers.size(); i++) {
            Customer customer = customers.get(i);
            Customer foundCustomer = foundCustomers.get(i);

            assertEquals(customer.getEmail(), foundCustomer.getEmail());
            assertEquals(customer.getFirstName(), foundCustomer.getFirstName());
            assertEquals(customer.getLastName(), foundCustomer.getLastName());
            assertNotNull("Supply points are set", foundCustomer.getSupplyPoints());
        }

        assertEquals(2, foundCustomers.get(0).getSupplyPoints().size());
        assertEquals(3, foundCustomers.get(1).getSupplyPoints().size());
        assertEquals(4, foundCustomers.get(2).getSupplyPoints().size());
    }

    protected CustomerRepository createCustomerRepository() {
        return new CustomerRepositoryImpl(dataSource);
    }

    protected SupplyPointRepository createSupplyPointRepository() {
        return new SupplyPointRepositoryImpl(dataSource);
    }

    protected List<Customer> createCustomersWithSupplyPoints() {
        List<Customer> customers = new ArrayList<>();
        Customer c1 = customerTestData.createCustomer("john.smith@gmail.com", "John", "Smith");
        Customer c2 = customerTestData.createCustomer("vanessa.twiggy@gmail.com", "Vanessa", "Twiggy");
        Customer c3 = customerTestData.createCustomer("jeremy.scott@gmail.com", "Jeremy", "Scott");
        SupplyPoint sp1 = supplyPointsTestData.createSupplyPoint("A1");
        SupplyPoint sp2 = supplyPointsTestData.createSupplyPoint("A2");
        SupplyPoint sp3 = supplyPointsTestData.createSupplyPoint("B1");
        SupplyPoint sp4 = supplyPointsTestData.createSupplyPoint("B2");
        SupplyPoint sp5 = supplyPointsTestData.createSupplyPoint("B3");
        SupplyPoint sp6 = supplyPointsTestData.createSupplyPoint("C1");

        c1.setSupplyPoints(CollectionFuns.list(sp1, sp2));
        c2.setSupplyPoints(CollectionFuns.list(sp3, sp4, sp5));
        c3.setSupplyPoints(CollectionFuns.list(sp6));

        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
        return customers;
    }

    protected void saveCustomersWithSupplyPoints(CustomerRepository repo, SupplyPointRepository spRepo, List<Customer> customers) {
        if (customers != null) {
            for (Customer c : customers) {
                repo.create(c, true);
                if (c.getSupplyPoints() != null) {
                    for (SupplyPoint sp : c.getSupplyPoints()) {
                        spRepo.create(sp, true);
                    }
                }
            }
        }
    }
}
