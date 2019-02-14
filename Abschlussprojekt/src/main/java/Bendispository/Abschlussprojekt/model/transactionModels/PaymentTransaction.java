package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

    private ConflictTransaction cfTrans;

    public void pay(Person leaser, Person lender, int amount){
        ProPaySubscriber pps = new ProPaySubscriber();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        if(transferIsOk){
            // Nachricht an Beteiligte, dass Zahlung erfolgt
            return;
        }
        cfTrans.addConflictTransaction();
    }
    public void isTransferIsOk(){

    }
}
