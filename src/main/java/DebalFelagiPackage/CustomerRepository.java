package DebalFelagiPackage;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findByZip(long zip);
    List<Customer> findByState(String st);
}
