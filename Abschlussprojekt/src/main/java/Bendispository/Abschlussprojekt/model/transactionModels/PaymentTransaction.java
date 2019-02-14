package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PaymentTransaction {

    // relies on ProPay
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private boolean transferIsOk;

    private boolean depositIsBlocked;


    private boolean depositIsReturned;

    private boolean lenderAccepted;

    @OneToOne
    private ConflictTransaction conflictTransaction;

    public void pay(Person leaser, Person lender, int amount){
        ProPaySubscriber pps = new ProPaySubscriber();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        if(transferIsOk){
            // Nachricht an Beteiligte, dass Zahlung erfolgt
            return;
        }
        conflictTransaction.addConflictTransaction();
    }
    /*
    public void isTransferIsOk(){

    }*/
}
