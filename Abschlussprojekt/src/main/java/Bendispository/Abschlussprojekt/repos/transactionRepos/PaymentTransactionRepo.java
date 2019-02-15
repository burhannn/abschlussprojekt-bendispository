package Bendispository.Abschlussprojekt.repos.transactionRepos;

import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;

import org.springframework.data.repository.CrudRepository;

public interface PaymentTransactionRepo extends CrudRepository<PaymentTransaction, Long> {
}
