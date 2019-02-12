package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.ConflictTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConflictTransactionRepo extends CrudRepository<ConflictTransaction, Long> {
    List<ConflictTransaction> findAll();
}
