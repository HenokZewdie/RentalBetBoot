package DebalFelagiPackage;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by student on 7/5/17.
 */
public interface UserRepository extends CrudRepository<User,Long> {
    User findByUsername(String username);
    Long countByEmail(String email);
    Long countByUsername(String username);

}
