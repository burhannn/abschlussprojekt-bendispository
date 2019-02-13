package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.Repo.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.Repo.RequestRepo;
import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Data
@Entity
public class Request {

    @Autowired
    RequestRepo requestRepo;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Person requester;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Item requestedItem;

    private int duration;

    // value = "denied", "approved", "pending"
    private RequestStatus status = RequestStatus.PENDING;

    private LeaseTransaction lsTrans;

    public void lenderApproved(){
        if(checkConclude() == true){
            Optional<Request> requestList = requestRepo.findById(id);
            Request request = requestList.get();
            lsTrans.addLeaseTransaction(request);
            setRequestOnApproved();
            requestedItem.setAvailable(false);
        }
        else{
            // Requester bekommt angezeigt, dass er nicht genügend Geld auf dem Konto hat
        }
    }

    public boolean checkConclude(){
        ProPaySubscriber pps = new ProPaySubscriber();
        if( pps.checkDeposit(requestedItem.getDeposit(), requester.getUsername())) return true;
        return false;
    }
    public void setRequestOnApproved(){
        setStatus(RequestStatus.APPROVED);
        setOtherRequestsOnDenied();
    }
    public void setOtherRequestsOnDenied(){
        List<Request> requestList = requestRepo.findAll();
        for(Request r  : requestList){
            if(r.requestedItem == requestedItem){
                setStatus(RequestStatus.DENIED);
            }
        }
    }

}