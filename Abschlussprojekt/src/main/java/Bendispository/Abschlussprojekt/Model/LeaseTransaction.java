package Bendispository.Abschlussprojekt.Model;

import Bendispository.Abschlussprojekt.Repo.RequestRepo;
import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;
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

    // number of days
    private int duration;

    private int depositId;

    private boolean itemIsReturned = false;

    private boolean itemIsReturnedOnTime = false;

    private ConcludeTransaction ccTrans;

    public void addLeaseTransaction(Request request){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(request.getRequestedItem());
        lsTrans.setLeaser(request.getRequester());
        lsTrans.setLender(request.getRequestedItem().getOwner());
        lsTrans.setDuration(request.getDuration());
        ccTrans.addConcludeTransaction();
    }

    public void itemReturnedToLender(){
        itemIsReturned = true;
        isReturnedOnTime();
        //zurückbuchung deposit
        payRent();
    }

    public void isReturnedOnTime(){
        // Berechnung Zeitspanne
    }

    public void payRent(){
        ProPaySubscriber pps = new ProPaySubscriber();
        int amount = duration * item.getCostPerDay();
        pps.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
    }
}
