package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Data
@Entity
public class LeaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person leaser;

    @ManyToOne(fetch = FetchType.EAGER)
    private Item item;

    private Long requestId;

    private int depositId;

    private boolean itemIsReturned = false;

    private boolean itemIsIntact = true;

    private boolean leaseIsConcluded = false;

    private int lengthOfTimeframeViolation = 0;

    private boolean timeframeViolation = false;

    //private boolean itemIsReturnedOnTime = false;

    // number of days
    private int duration;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany
    private List<PaymentTransaction> payments;

    @OneToOne(cascade = CascadeType.PERSIST,
              fetch = FetchType.EAGER)
    private ConflictTransaction conflictTransaction;

    public void addLeaseTransaction(Request request, int depositId){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(request.getRequestedItem());
        lsTrans.setLeaser(request.getRequester());
        lsTrans.setRequestId(request.getId());
        lsTrans.setDuration(request.getDuration());
        lsTrans.startDate = request.getStartDate();
        lsTrans.endDate = request.getEndDate();
        lsTrans.depositId = depositId;
        request.getRequester().addLeaseTransaction(lsTrans);
    }

    public void addPaymentTransaction(PaymentTransaction paymentTransaction){
        this.payments.add(paymentTransaction);
    }

    /*public void itemReturnedToLender(PaymentTransactionRepo paymentTransactionRepo){
        itemIsReturned = true;
        //zurückbuchung deposit

        int amount = duration * item.getCostPerDay();
        PaymentTransaction paymentTransaction = new PaymentTransaction(leaser, lender, amount);
        paymentTransaction.pay(leaser, lender, this);

        isReturnedOnTime(paymentTransactionRepo);
    }

    public void isReturnedOnTime(PaymentTransactionRepo paymentTransactionRepo){
        if(LocalDate.now().isAfter(endDate)){
            Period period = Period.between(LocalDate.now(), endDate);
            int timeViolation = period.getDays();
            concludeTransaction.setTimeframeViolation(true);
            concludeTransaction.setLengthOfTimeframeViolation(timeViolation);

            int amount = item.getCostPerDay() * timeViolation;
            PaymentTransaction paymentTransaction = new PaymentTransaction(leaser, lender, amount);
            paymentTransaction.pay(paymentTransactionRepo);
        }
    }*/
}
