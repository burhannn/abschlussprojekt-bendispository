package Bendispository.Abschlussprojekt.Model;

import Bendispository.Abschlussprojekt.Repo.RequestRepo;
import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

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

    private LocalDate dayOfRent;

    private ConcludeTransaction ccTrans;

    public void addLeaseTransaction(Request request){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(request.getRequestedItem();
        lsTrans.setLeaser(request.getRequester());
        lsTrans.setLender(request.getRequestedItem().getOwner());
        lsTrans.setDuration(request.getDuration());
        lsTrans.dayOfRent = LocalDate.now();
        ccTrans.addConcludeTransaction();
    }

    public void itemReturnedToLender(){
        itemIsReturned = true;
        isReturnedOnTime();
        //zurückbuchung deposit
        payRent();
        ccTrans.overTimeFee(leaser, lender, item);
    }

    public void isReturnedOnTime(){
        if(LocalDate.now().isAfter(dayOfRent.plusDays(duration))){
            Period period = Period.between(LocalDate.now(), dayOfRent.plusDays(duration));
            int timeViolation = period.getDays();
            ccTrans.isTimeframeViolation(true);
            ccTrans.setLengthOfTimeframeViolation(timeViolation);
        }
    }

    public void payRent(){
        ProPaySubscriber pps = new ProPaySubscriber();
        int amount = duration * item.getCostPerDay();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
    }
}
