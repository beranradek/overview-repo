package cz.etn.overview.sql.mapper;

import cz.etn.overview.VoucherTestDb;
import cz.etn.overview.data.CustomerTestData;
import cz.etn.overview.data.SupplyPointTestData;
import cz.etn.overview.domain.Customer;
import cz.etn.overview.domain.SupplyPoint;
import cz.etn.overview.sql.repo.CustomerRepository;
import cz.etn.overview.sql.repo.CustomerRepositoryImpl;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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


    }

    protected CustomerRepository createCustomerRepository() {
        return new CustomerRepositoryImpl(dataSource);
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
        return customers;
    }
}
