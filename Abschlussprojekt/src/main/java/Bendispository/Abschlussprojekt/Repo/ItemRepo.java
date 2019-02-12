package Bendispository.Abschlussprojekt.repo;

import Bendispository.Abschlussprojekt.Model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepo extends CrudRepository<Item,Long> {
    List<Item> findAll();
}
