package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@Entity
public class ConflictTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private boolean lenderAccepted;

    private boolean leaserAccepted;

    private int damageCosts;

    private int validationTime;

    @OneToOne
    PaymentTransaction paymentTransaction;

    public void addConflictTransaction(PaymentTransaction paymentTransaction) {
        ConflictTransaction conflictTransaction = new ConflictTransaction();
        this.paymentTransaction = paymentTransaction;
    }

    public void addConflictTransaction(){
        ConflictTransaction conflictTransaction = new ConflictTransaction();
    }
    public void conflictSolved(PaymentTransactionRepo paymentTransactionRepo){
        if(leaserAccepted && lenderAccepted){
            paymentTransaction.setAmount(damageCosts);
            paymentTransaction.pay(paymentTransactionRepo);
        }
    }

}