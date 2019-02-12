package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequestRepo extends CrudRepository<Request, Long>{
    List<Request> findAll();
}
