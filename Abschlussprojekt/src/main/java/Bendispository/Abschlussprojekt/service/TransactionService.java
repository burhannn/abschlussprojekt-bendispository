package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentType;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class TransactionService {

    private final RequestRepo requestRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private final PaymentTransactionRepo paymentTransactionRepo;

    private ProPaySubscriber proPaySubscriber;

    @Autowired
    public TransactionService(LeaseTransactionRepo leaseTransactionRepo,
                              RequestRepo requestRepo,
                              ProPaySubscriber proPaySubscriber,
                              PaymentTransactionRepo paymentTransactionRepo) {
        super();
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.requestRepo = requestRepo;
        this.proPaySubscriber = proPaySubscriber;
        this.paymentTransactionRepo = paymentTransactionRepo;
    }


    public boolean lenderApproved(Request request){
        int deposit = request.getRequestedItem().getDeposit();
        Person requester = request.getRequester();
        if(proPaySubscriber.checkDeposit(deposit,
                                         requester.getUsername())) {

            int depositId = proPaySubscriber.makeDeposit(request);

            PaymentTransaction paymentTransaction = new PaymentTransaction(requester,
                                                                           request.getRequestedItem().getOwner(),
                                                                           deposit);
            paymentTransaction.setType(PaymentType.DEPOSIT);
            paymentTransactionRepo.save(paymentTransaction);

            LeaseTransaction leaseTransaction = new LeaseTransaction();
            leaseTransaction.addLeaseTransaction(request, depositId);
            //leaseTransaction.addPaymentTransaction(paymentTransaction);
            paymentTransaction.setLeaseTransaction(leaseTransaction);

            paymentTransactionRepo.save(paymentTransaction);
            leaseTransactionRepo.save(leaseTransaction);

            request.setLeaseTransaction(leaseTransaction);
            setRequestApproved(request);
            requestRepo.save(request);

            return true;
        }
        return false;
    }

    private void setRequestApproved(Request request){
        setOtherRequestsOnDenied(request);
        request.setStatus(RequestStatus.APPROVED);
    }

    private void setOtherRequestsOnDenied(Request request) {
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
    private static boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public void itemReturnedToLender(LeaseTransaction leaseTransaction){
        Person leaser = leaseTransaction.getLeaser();
        Person lender = leaseTransaction.getItem().getOwner();
        leaseTransaction.setItemIsReturned(true);

        //zurückbuchung deposit

        int amount = leaseTransaction.getDuration() * leaseTransaction.getItem().getCostPerDay();
        makePayment(leaser, lender, amount, leaseTransaction, PaymentType.RENTPRICE);

        // 1. zeitgemäß?
        isReturnedInTime(leaseTransaction, leaser, lender);

        // 2. intakt? ja, vorbei => kaution rückbuchen; nein => konfliktstelle!

    }

    public void isReturnedInTime(LeaseTransaction leaseTransaction, Person leaser, Person lender){
        if(LocalDate.now().isAfter(leaseTransaction.getEndDate())){
            Period period = Period.between(leaseTransaction.getEndDate(), LocalDate.now());
            int timeViolation = period.getDays();
            leaseTransaction.setTimeframeViolation(true);
            leaseTransaction.setLengthOfTimeframeViolation(timeViolation);

            int amount = leaseTransaction.getItem().getCostPerDay() * timeViolation;
            makePayment(leaser, lender, amount, leaseTransaction, PaymentType.DAMAGES);

        }
    }

    private void makePayment(Person leaser, Person lender, int amount, LeaseTransaction leaseTransaction, PaymentType type){
        PaymentTransaction paymentTransaction = new PaymentTransaction(leaser, lender, amount);
        paymentTransaction.setType(type);
        paymentTransaction.setLeaseTransaction(leaseTransaction);
        paymentTransactionRepo.save(paymentTransaction);
        proPaySubscriber.transferMoney(leaser.getUsername(), lender.getUsername(), amount);
    }

}
