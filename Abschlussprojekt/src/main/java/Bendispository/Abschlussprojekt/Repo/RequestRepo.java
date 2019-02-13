package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.Person;
import Bendispository.Abschlussprojekt.Model.Request;
import Bendispository.Abschlussprojekt.Model.RequestStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepo extends CrudRepository<Request, Long>{
    List<Request> findAll();

    List<Request> findByRequester(Person requester);

    List<Request> findByRequestedItemOwner(Person provider);

    List<Request> findByRequesterAndStatus(Person person, RequestStatus status);
}
