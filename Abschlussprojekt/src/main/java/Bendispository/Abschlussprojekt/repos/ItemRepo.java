package Bendispository.Abschlussprojekt.repos;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepo extends CrudRepository<Item, Long> {
	List<Item> findAll();

	List<Item> findByOwner(Person loggedIn);

	List<Item> findByOwnerNotAndActiveTrue(Person loggedIn);
}
