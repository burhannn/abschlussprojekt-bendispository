package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@Entity
public class Request {

    // Tage, kann auf stunden gewechselt werden
    private static final int VALIDATION = 2;

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

    private LocalDate startDate;

    private LocalDate endDate;

    //Zeit, die der Lender hat, um den Request zu bearbeiten
    private int validationTime = VALIDATION;

    // value = "denied", "approved", "pending"
    private RequestStatus status = RequestStatus.PENDING;

    @OneToOne(cascade = CascadeType.PERSIST,
              fetch = FetchType.EAGER)
    private LeaseTransaction leaseTransaction;

    public void lenderApproved(RequestRepo requestRepo){
        if(checkConclude() == true){
            Optional<Request> requestList = requestRepo.findById(id);
            Request request = requestList.get();
            leaseTransaction.addLeaseTransaction(request);
            setRequestOnApproved(requestRepo);
            requestedItem.setAvailable(false); //nur für duration auf false setzen
        }
        else{
            // Requester bekommt angezeigt, dass er nicht genügend Geld auf dem Konto hat
            setStatus(RequestStatus.DENIED);
        }
    }

    public boolean checkConclude(){
        ProPaySubscriber pps = new ProPaySubscriber();
        if( pps.checkDeposit(requestedItem.getDeposit(), requester.getUsername())) {
            //deposit blocken
            return true;
        }
        return false;
    }
    public void setRequestOnApproved(RequestRepo requestRepo){
        setStatus(RequestStatus.APPROVED);
        setOtherRequestsOnDenied(requestRepo);
    }
    public void setOtherRequestsOnDenied(RequestRepo requestRepo) {
        List<Request> requestList = requestRepo.findAll();
        for(Request r  : requestList){
            if(r.requestedItem == requestedItem){
                setStatus(RequestStatus.DENIED);
            }
        }
    }

}
