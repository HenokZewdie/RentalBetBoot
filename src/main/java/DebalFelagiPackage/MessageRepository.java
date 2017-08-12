package DebalFelagiPackage;

import DebalFelagiPackage.MessageSend;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageSend, Long>{
}
