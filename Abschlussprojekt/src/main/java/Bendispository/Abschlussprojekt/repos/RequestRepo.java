package Bendispository.Abschlussprojekt.repos;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepo extends CrudRepository<Request, Long>{
    List<Request> findAll();

    List<Request> findByRequester(Person requester);

    List<Request> findByRequestedItemOwner(Person provider);

    List<Request> findByRequesterAndStatus(Person person, RequestStatus status);
}
