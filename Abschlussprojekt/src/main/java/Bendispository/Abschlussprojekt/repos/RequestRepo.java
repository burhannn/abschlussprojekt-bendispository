package Bendispository.Abschlussprojekt.repos;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepo extends CrudRepository<Request, Long>{
    List<Request> findAll();

    List<Request> findAllByRequestedItem(Item item);

    List<Request> findByRequester(Person requester);

    List<Request> findByRequesterAndStatus(Person requester, RequestStatus status);

    List<Request> findByRequestedItemOwner(Person provider);

    List<Request> findByRequestedItemOwnerAndStatus(Person provider, RequestStatus status);

    List<Request> findByRequesterAndRequestedItemAndStatus(Person person, Item item, RequestStatus status);
}
