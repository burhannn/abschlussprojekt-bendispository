package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ConcludeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private int lengthOfTimeframeViolation;

    private boolean timeframeViolation;

    private boolean depositIsBlocked;

    private boolean depositIsReturned;

    private boolean lenderAccepted;

    private ConflictTransaction cfTransaction;

    public void overTimeFee(Person leaser, Person lender, Item item) {
        if (timeframeViolation) {
            ProPaySubscriber pps = new ProPaySubscriber();
            int amount = item.getCostPerDay() * lengthOfTimeframeViolation;
            pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        }
    }

    public void addConcludeTransaction(){
        ConcludeTransaction ccTrans = new ConcludeTransaction();
        checkDepositIsBlocked();
    }

    public void checkDepositIsBlocked(){

    }

    public void checkTransactionIsOk(){
        if(lenderAccepted == false)
            cfTransaction.addConflictTransaction();
    }
}
