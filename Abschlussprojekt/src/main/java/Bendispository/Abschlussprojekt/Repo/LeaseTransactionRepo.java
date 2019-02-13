package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.Item;
import Bendispository.Abschlussprojekt.Model.LeaseTransaction;
import Bendispository.Abschlussprojekt.Model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LeaseTransactionRepo extends CrudRepository<LeaseTransaction, Long> {
    List<LeaseTransaction> findAll();

    Optional<LeaseTransaction> findByRequestId(Long id);
}
