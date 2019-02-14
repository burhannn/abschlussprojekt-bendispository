package Bendispository.Abschlussprojekt.repo;

import Bendispository.Abschlussprojekt.model.ConcludeTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConcludeTransactionRepo extends CrudRepository<ConcludeTransaction, Long> {
    List<ConcludeTransaction> findAll();
}
