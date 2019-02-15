package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentService {

    final PaymentTransactionRepo paymentTransactionRepo;

    @Autowired
    public PaymentService(PaymentTransactionRepo paymentTransactionRepo) {
        super();
        this.paymentTransactionRepo = paymentTransactionRepo;
    }

    /*public void pay(Person leaser, Person lender, ){
        ProPaySubscriber pps = new ProPaySubscriber();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        if(transferIsOk){
            // Nachricht an Beteiligte, dass Zahlung erfolgt
            return;
        }
        Optional<PaymentTransaction> payment = paymentTransactionRepo.findById(id);
        conflictTransaction.addConflictTransaction(payment.get());
    }*/

}
