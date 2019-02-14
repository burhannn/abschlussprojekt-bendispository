package Bendispository.Abschlussprojekt.repo;

import Bendispository.Abschlussprojekt.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonsRepo extends CrudRepository<Person,Long> {
    List<Person> findAll();
}
