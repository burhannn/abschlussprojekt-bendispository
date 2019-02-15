package Bendispository.Abschlussprojekt.model.transactionModels;

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
