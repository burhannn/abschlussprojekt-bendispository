package Bendispository.Abschlussprojekt.repos;

import Bendispository.Abschlussprojekt.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonsRepo extends CrudRepository<Person,Long> {
    List<Person> findAll();
    List<Person> findAllByUsernameNotAndUsernameNot(String username, String admin);
    Person findByUsername(String username);
}
