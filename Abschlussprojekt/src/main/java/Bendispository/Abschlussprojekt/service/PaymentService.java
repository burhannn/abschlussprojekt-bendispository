package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    final PaymentTransactionRepo paymentTransactionRepo;

    @Autowired
    public PaymentService(PaymentTransactionRepo paymentTransactionRepo) {
        super();
        this.paymentTransactionRepo = paymentTransactionRepo;
    }
}
