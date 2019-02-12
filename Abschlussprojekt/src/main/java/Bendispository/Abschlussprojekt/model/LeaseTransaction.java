package Bendispository.Abschlussprojekt.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class LeaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Person leaser;

    @OneToOne(cascade = CascadeType.PERSIST,
              fetch = FetchType.EAGER)
    private Person lender;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Item item;

    // number of days
    private int duration;

    private boolean itemIsReturned = false;

    private boolean itemIsReturnedOnTime = false;

    private boolean itemIsIntact = false;

    public LeaseTransaction addTransaction(Request request){
        LeaseTransaction lsTrans = new LeaseTransaction();
        lsTrans.setItem(request.getRequestedItem());
        lsTrans.setLeaser(request.getRequester());
        lsTrans.setLender(request.getRequestedItem().getOwner());
        lsTrans.setDuration(request.getDuration());
        return lsTrans;
    }

}
