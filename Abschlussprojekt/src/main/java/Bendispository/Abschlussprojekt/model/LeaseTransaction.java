package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;
import java.rmi.dgc.Lease;

@Data
@Entity
public class LeaseTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @@OneToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private Person leaser;

    @OneToOne(cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    private Person lender;

    @OneToOne(cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    private Item item;

    private int duration;

    private boolean itemReturned = false;

    private boolean depositReturned = false;

    public LeaseTransaction addTransaction(Person leaser, Item item, Request request){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(item);
        lsTrans.setLeaser(leaser);
        lsTrans.setLender(item.owner);
        lsTrans.setDuration(request.duration);
        return lsTrans;
    }

}
