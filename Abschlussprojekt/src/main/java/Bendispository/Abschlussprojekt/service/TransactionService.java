package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TransactionService {

    private final RequestRepo requestRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private ProPaySubscriber proPaySubscriber;

    @Autowired
    public TransactionService(LeaseTransactionRepo leaseTransactionRepo,
                              RequestRepo requestRepo,
                              ProPaySubscriber proPaySubscriber) {
        super();
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.requestRepo = requestRepo;
        this.proPaySubscriber = proPaySubscriber;
    }


    public boolean lenderApproved(Request request){
        if(proPaySubscriber.checkDeposit(request.getRequestedItem().getDeposit(),
                                         request.getRequester().getUsername())) {

            int depositId = proPaySubscriber.makeDeposit(request);

            LeaseTransaction leaseTransaction = new LeaseTransaction();
            leaseTransaction.addLeaseTransaction(request, depositId);
            leaseTransactionRepo.save(leaseTransaction);

            request.setLeaseTransaction(leaseTransaction);
            setRequestApproved(request);
            requestRepo.save(request);
            return true;
        }
        return false;
    }

    public void setRequestApproved(Request request){
        setOtherRequestsOnDenied(request);
        request.setStatus(RequestStatus.APPROVED);
    }

    public void setOtherRequestsOnDenied(Request request) {
        List<Request> requestList = requestRepo.findAllByRequestedItem(request.getRequestedItem());
        for(Request r  : requestList)
            if(isOverlapping(r.getStartDate(), r.getEndDate(),
                             request.getStartDate(), request.getEndDate()))
                r.setStatus(RequestStatus.DENIED);
    }

    public boolean itemIsAvailableOnTime(Request request) {
        List<LeaseTransaction> leaseTransactionList = leaseTransactionRepo.findAllByItemId(request.getRequestedItem().getId());
        for(LeaseTransaction l  : leaseTransactionList)
            if(isOverlapping(l.getStartDate(), l.getEndDate(),
                    request.getStartDate(), request.getEndDate()))
                return false;
        return true;
    }

    // Disclaimer: https://stackoverflow.com/a/17107966
    public static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

}
