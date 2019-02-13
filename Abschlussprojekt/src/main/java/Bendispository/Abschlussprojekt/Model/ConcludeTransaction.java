package Bendispository.Abschlussprojekt.Model;

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

    private boolean depositIsReturned;

    private boolean lenderAccepted;


    public void conclude (LeaseTransaction leaseTransaction){
        this.timeframeViolation = checkTimeFrameViolation(leaseTransaction);
    }

    private boolean checkTimeFrameViolation(LeaseTransaction leaseTransaction) {

        return false;
    }
}
