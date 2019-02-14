package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.model.Item;
import lombok.Data;

import javax.persistence.*;

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

    public void overTimeFee(Person leaser, Person lender, Item item) {
        if (timeframeViolation) {
            ProPaySubscriber pps = new ProPaySubscriber();
            int amount = item.getCostPerDay() * lengthOfTimeframeViolation;
            pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
        }
    }

    @OneToOne(cascade = CascadeType.PERSIST,
              fetch = FetchType.EAGER)
    private ConflictTransaction cfTransaction;

    public void addConcludeTransaction(){
        ConcludeTransaction ccTrans = new ConcludeTransaction();
        checkDepositIsBlocked();
    }

    public void checkDepositIsBlocked(){

    }

    public void checkTransactionIsOk(){
        if(lenderAccepted == false){
            cfTransaction.addConflictTransaction();
        }
    }
}
