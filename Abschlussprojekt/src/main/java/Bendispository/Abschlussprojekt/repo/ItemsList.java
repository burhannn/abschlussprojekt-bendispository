package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemsList extends CrudRepository<Item,Long> {
    List<Item> findAll();
}
