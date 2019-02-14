package Bendispository.Abschlussprojekt.repo;

import Bendispository.Abschlussprojekt.model.PaymentTransaction;
import org.springframework.data.repository.CrudRepository;

public interface PaymentTransactionRepo extends CrudRepository<PaymentTransaction, Long> {
}
