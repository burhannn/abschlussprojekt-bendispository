package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@Entity
public class PaymentTransaction {

    // relies on ProPay
    @Autowired
    PaymentTransactionRepo paymentTransactionRepo;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private Person leaser;

    private Person lender;

    private int amount;

    private boolean transferIsOk;

    private boolean depositIsBlocked;


    private boolean depositIsReturned;

    private boolean lenderAccepted;

    @OneToOne
    private ConflictTransaction conflictTransaction;

    public PaymentTransaction(Person leaser, Person lender, int amount){
        this.leaser = leaser;
        this.lender = lender;
        this.amount = amount;

    }
    public void pay(){
        ProPaySubscriber pps = new ProPaySubscriber();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        if(transferIsOk){
            // Nachricht an Beteiligte, dass Zahlung erfolgt
            return;
        }
        Optional<PaymentTransaction> payment = paymentTransactionRepo.findById(id);
        conflictTransaction.addConflictTransaction(payment.get());
    }
    /*
    public void isTransferIsOk(){

    }*/
}
