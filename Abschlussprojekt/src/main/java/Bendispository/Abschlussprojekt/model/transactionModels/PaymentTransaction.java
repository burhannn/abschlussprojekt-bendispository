package Bendispository.Abschlussprojekt.model.transactionModels;

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

    private boolean isPayed;

    private boolean depositIsBlocked;


    private boolean depositIsReturned;

    private boolean lenderAccepted;


    /*public void conclude (LeaseTransaction leaseTransaction){
        this.timeframeViolation = checkTimeFrameViolation(leaseTransaction);
    }*/

    private boolean checkTimeFrameViolation(LeaseTransaction leaseTransaction) {

        return false;
    }
}
