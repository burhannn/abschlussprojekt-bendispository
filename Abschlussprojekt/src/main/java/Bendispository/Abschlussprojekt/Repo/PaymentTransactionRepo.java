package Bendispository.Abschlussprojekt.Repo;

import Bendispository.Abschlussprojekt.Model.PaymentTransaction;
import org.springframework.data.repository.CrudRepository;

public interface PaymentTransactionRepo extends CrudRepository<PaymentTransaction, Long> {
}
