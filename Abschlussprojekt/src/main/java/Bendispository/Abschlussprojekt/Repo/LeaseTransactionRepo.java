package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.LeaseTransaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeaseTransactionRepo extends CrudRepository<LeaseTransaction, Long> {
    List<LeaseTransaction> findAll();
}
