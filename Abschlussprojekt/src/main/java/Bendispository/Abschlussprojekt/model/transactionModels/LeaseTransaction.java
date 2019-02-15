package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Data
@Entity
public class LeaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(
               fetch = FetchType.EAGER)
    private Person leaser;

    @ManyToOne(
              fetch = FetchType.EAGER)
    private Person lender;

    @ManyToOne(
               fetch = FetchType.EAGER)
    private Item item;

    private Long requestId;

    // number of days
    private int duration;

    private int depositId;

    private boolean itemIsReturned = false;

    //private boolean itemIsReturnedOnTime = false;

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToOne(cascade = CascadeType.PERSIST,
              fetch = FetchType.EAGER)
    private ConcludeTransaction concludeTransaction;

    public void addLeaseTransaction(Request request){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(request.getRequestedItem());
        lsTrans.setLeaser(request.getRequester());
        lsTrans.setLender(request.getRequestedItem().getOwner());
        lsTrans.setDuration(request.getDuration());
        lsTrans.startDate = request.getStartDate();
        lsTrans.endDate = request.getEndDate();
        concludeTransaction.addConcludeTransaction();
    }

    public void itemReturnedToLender(){
        itemIsReturned = true;
        //zurückbuchung deposit

        int amount = duration * item.getCostPerDay();
        PaymentTransaction pay = new PaymentTransaction(leaser, lender, amount);
        pay.pay();

        isReturnedOnTime();
    }

    public void isReturnedOnTime(){
        if(LocalDate.now().isAfter(endDate)){
            Period period = Period.between(LocalDate.now(), endDate);
            int timeViolation = period.getDays();
            concludeTransaction.setTimeframeViolation(true);
            concludeTransaction.setLengthOfTimeframeViolation(timeViolation);

            int amount = item.getCostPerDay() * timeViolation;
            PaymentTransaction pay = new PaymentTransaction(leaser, lender, amount);
            pay.pay();
        }
    }
}
