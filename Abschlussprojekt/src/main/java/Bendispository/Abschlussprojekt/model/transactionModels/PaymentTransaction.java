package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;
import java.util.Optional;

@Data
@Entity
public class PaymentTransaction {

    // relies on ProPay
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    private Person leaser;

    @ManyToOne
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
    public void pay(PaymentTransactionRepo paymentTransactionRepo){
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
