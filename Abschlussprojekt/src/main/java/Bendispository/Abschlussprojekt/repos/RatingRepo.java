package Bendispository.Abschlussprojekt.repos;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RatingRepo extends CrudRepository<Rating, Long> {
	List<Rating> findAllBy();

	List<Rating> findAllByRater(Person loggedIn);
}
