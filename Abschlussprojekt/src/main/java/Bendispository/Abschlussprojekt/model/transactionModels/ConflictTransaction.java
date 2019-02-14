package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ConflictTransaction {

    @Autowired
    PaymentTransactionRepo paymentTransactionRepo;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private boolean lenderAccepted;

    private boolean leaserAccepted;

    private int damageCosts;

    private int validationTime;

    PaymentTransaction paymentTransaction;

    public void addConflictTransaction(PaymentTransaction paymentTransaction) {
        ConflictTransaction conflictTransaction = new ConflictTransaction();
        this.paymentTransaction = paymentTransaction;
    }

    public void addConflictTransaction(){
        ConflictTransaction conflictTransaction = new ConflictTransaction();
    }
    public void conflictSolved(){
        if(leaserAccepted && lenderAccepted){
            paymentTransaction.setAmount(damageCosts);
            paymentTransaction.pay();
        }
    }

}