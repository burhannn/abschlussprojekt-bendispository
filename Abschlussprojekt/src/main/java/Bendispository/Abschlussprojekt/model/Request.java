package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@Entity
public class Request {

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

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private int validationTime; //Zeit, die der Lender hat, um den Request zu bearbeiten

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