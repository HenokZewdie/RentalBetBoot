package DebalFelagiPackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by student on 6/21/17.
 */
public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findByZipCode(long zip);
    List<House> findByState (String State);
    List<House> findByCity(String city);
   // List<House> findByZipCode(String zip);
    List<House> findById(long id);
    //List<House> findByType(String typ);
    //List<House> findByCityOrStateOrZipCodeAndType(@Param("city") String city)
    List<House> findByStateAndType(String state, String type);
    //@Query(value = "SELECT bath, kitchen,id from house h where h.type = 'apartment'", nativeQuery = true)


}
