package Bendispository.Abschlussprojekt.repo;

import Bendispository.Abschlussprojekt.model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepo extends CrudRepository<Request, Long>{
    List<Request> findAll();
}
