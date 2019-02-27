package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.*;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
public class TransactionService {

    private final RequestRepo requestRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private final PaymentTransactionRepo paymentTransactionRepo;

    private final ConflictTransactionRepo conflictTransactionRepo;

    private RatingRepo ratingRepo;

    private ProPaySubscriber proPaySubscriber;

    private Clock clock;

    @Autowired
    public TransactionService(LeaseTransactionRepo leaseTransactionRepo,
                              RequestRepo requestRepo,
                              ProPaySubscriber proPaySubscriber,
                              PaymentTransactionRepo paymentTransactionRepo,
                              ConflictTransactionRepo conflictTransactionRepo,
                              RatingRepo ratingRepo,
                              Clock clock) {
        super();
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.requestRepo = requestRepo;
        this.proPaySubscriber = proPaySubscriber;
        this.paymentTransactionRepo = paymentTransactionRepo;
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.ratingRepo = ratingRepo;
        this.clock = clock;
    }

    public boolean lenderApproved(Request request){
        double deposit = (double) request.getRequestedItem().getDeposit();
        Person requester = request.getRequester();

        if(proPaySubscriber.checkDeposit(deposit,
                                         requester.getUsername())) {
            int depositId = proPaySubscriber.makeDeposit(request);
            if(depositId == -1){
                return false;
            }
            LeaseTransaction leaseTransaction = new LeaseTransaction();
            leaseTransaction.addLeaseTransaction(request, depositId);

            PaymentTransaction payment = makePayment(requester, request.getRequestedItem().getOwner(),
                                                                deposit, leaseTransaction, PaymentType.DEPOSIT);
            leaseTransaction.addPaymentTransaction(payment);
            leaseTransactionRepo.save(leaseTransaction);

            request.setLeaseTransaction(leaseTransaction);
            setRequestApproved(request);
            createRating(request);
            requestRepo.save(request);
            return true;
        }
        return false;
    }

    protected void createRating(Request request){
        Rating rating1 = new Rating();
        rating1.setRequest(request);
        rating1.setRater(request.getRequester());

        Rating rating2 = new Rating();
        rating2.setRequest(request);
        rating2.setRater(request.getRequestedItem().getOwner());

        ratingRepo.save(rating1);
        ratingRepo.save(rating2);
    }

    protected void setRequestApproved(Request request){
        setOtherRequestsOnDenied(request);
        request.setStatus(RequestStatus.APPROVED);
    }

    protected void setOtherRequestsOnDenied(Request request) {
        List<Request> requestList = requestRepo.findAllByRequestedItem(request.getRequestedItem());
        for(Request r  : requestList)
            if(isOverlapping(r.getStartDate(), r.getEndDate(),
                             request.getStartDate(), request.getEndDate()))
                r.setStatus(RequestStatus.DENIED);
    }

    public boolean itemIsAvailableOnTime(Request request) {
        List<LeaseTransaction> leaseTransactionList =
                leaseTransactionRepo
                        .findAllByItemId(request.getRequestedItem().getId());
        for(LeaseTransaction l  : leaseTransactionList)
            if(isOverlapping(l.getStartDate(), l.getEndDate(),
                    request.getStartDate(), request.getEndDate()))
                return false;
        return true;
    }

    // Disclaimer: https://stackoverflow.com/a/17107966
    public boolean isOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return (start1.isBefore(end2) && start2.isBefore(end1));
    }

    public boolean itemReturnedToLender(LeaseTransaction leaseTransaction){
        Person leaser = leaseTransaction.getLeaser();
        Person lender = leaseTransaction.getItem().getOwner();

        double amount = (double) leaseTransaction.getDuration() * leaseTransaction.getItem().getCostPerDay();
        if( !(proPaySubscriber.transferMoney(leaser.getUsername(), lender.getUsername(), amount))) {
            return false;
        }
        leaseTransaction.setItemIsReturned(true);
        PaymentTransaction payment = makePayment(leaser, lender, amount,
                                                 leaseTransaction, PaymentType.RENTPRICE);
        leaseTransaction.addPaymentTransaction(payment);
        if( !(isReturnedInTime(leaseTransaction, leaser, lender))){
            return false;
        }
        leaseTransactionRepo.save(leaseTransaction);
        return true;
    }

    // boolean is with regards to ProPay,
    // not about returning in time!
    protected boolean isReturnedInTime(LeaseTransaction leaseTransaction, Person leaser, Person lender){
        if(isTimeViolation(leaseTransaction)){
            double amount =
                    leaseTransaction.getItem().getCostPerDay()
                            * leaseTransaction.getLengthOfTimeframeViolation();
            if( !(proPaySubscriber.transferMoney(leaser.getUsername(), lender.getUsername(), amount))){
                return false;
            }
            PaymentTransaction payment = makePayment(leaser, lender, amount, leaseTransaction, PaymentType.DAMAGES);
            leaseTransaction.addPaymentTransaction(payment);
        }
        return true;
    }

    public boolean itemIsIntact(LeaseTransaction leaseTransaction){
        ProPayAccount account = proPaySubscriber
                .releaseReservation(
                        leaseTransaction.getLeaser().getUsername(),
                        leaseTransaction.getDepositId());
        if(account == null)
            return false;
        conclude(leaseTransaction);
        return true;
    }

    public boolean itemIsNotIntactConclusion(LeaseTransaction leaseTransaction) {
        ProPayAccount account = proPaySubscriber
                .releaseReservationAndPunishUser(
                        leaseTransaction.getLeaser().getUsername(),
                        leaseTransaction.getDepositId());
        if(account == null)
            return false;
        conclude(leaseTransaction);
        return true;
    }

    private void conclude(LeaseTransaction leaseTransaction){
        for (PaymentTransaction payment : leaseTransaction.getPayments()){
            if(payment.getType() == PaymentType.DEPOSIT){
                payment.setPaymentIsConcluded(true);
                paymentTransactionRepo.save(payment);
                break;
            }
        }
        leaseTransaction.setLeaseIsConcluded(true);
        leaseTransactionRepo.save(leaseTransaction);
    }

    protected PaymentTransaction makePayment(Person leaser, Person lender, double amount,
                                           LeaseTransaction leaseTransaction, PaymentType type){
        PaymentTransaction paymentTransaction = new PaymentTransaction(leaser, lender, amount);
        paymentTransaction.setType(type);
        paymentTransaction.setLeaseTransaction(leaseTransaction);
        if(!(type == PaymentType.DEPOSIT)) {
            paymentTransaction.setPaymentIsConcluded(true);
        }
        paymentTransactionRepo.save(paymentTransaction);
        return paymentTransaction;
    }

    public ConflictTransaction notIntact(Long id, String comment, Person me){
        LeaseTransaction leaseTransaction = leaseTransactionRepo
                .findById(id)
                .orElse(new LeaseTransaction());
        leaseTransaction.setLeaseIsConcluded(true);
        // otherwise these will still show up in overview of returned items.
        return itemIsNotIntact(me, leaseTransaction, comment);
    }

    public ConflictTransaction itemIsNotIntact(Person me, LeaseTransaction leaseTransaction, String commentary) {
        ConflictTransaction conflictTransaction = new ConflictTransaction();
        conflictTransaction.setLeaseTransaction(leaseTransaction);
        conflictTransaction.setCommentary(commentary);
        conflictTransactionRepo.save(conflictTransaction);
        return conflictTransaction;
    }

    public boolean isTimeViolation(LeaseTransaction leaseTransaction) {
        if(LocalDate.now(clock).isAfter(leaseTransaction.getEndDate())) {
            Period period = Period.between(leaseTransaction.getEndDate(), LocalDate.now(clock));
            int timeViolation = period.getDays();
            leaseTransaction.setTimeframeViolation(true);
            leaseTransaction.setLengthOfTimeframeViolation(timeViolation);
            return true;
        }
        return false;
    }


}
