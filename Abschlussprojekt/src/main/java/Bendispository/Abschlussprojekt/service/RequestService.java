package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Bendispository.Abschlussprojekt.model.RequestStatus.AWAITING_SHIPMENT;

@Component
public class RequestService {

    PersonsRepo personsRepo;

    RequestRepo requestRepo;

    LeaseTransactionRepo leaseTransactionRepo;

    private final AuthenticationService authenticationService;

    private final ItemRepo itemRepo;

    private final ProPaySubscriber proPaySubscriber;

    private final TransactionService transactionService;

    private Clock clock;

    @Autowired
    public RequestService(PersonsRepo personsRepo,
                          RequestRepo requestRepo,
                          ItemRepo itemRepo,
                          AuthenticationService authenticationService,
                          Clock clock,
                          TransactionService transactionService,
                          ProPaySubscriber proPaySubscriber,
                          LeaseTransactionRepo leaseTransactionRepo){
        this.personsRepo = personsRepo;
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.authenticationService = authenticationService;
        this.proPaySubscriber = proPaySubscriber;
        this.transactionService = transactionService;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.clock = clock;
    }

    public List<Request> deleteObsoleteRequests(List<Request> myRequests) {
        List<Request> toRemove = new ArrayList<>();
        for(Request request : myRequests){
            if(request.getStartDate().isBefore(LocalDate.now(clock))) {
                toRemove.add(request);
            }
        }
        requestRepo.deleteAll(toRemove);
        return myRequests.stream().filter(i -> !(toRemove.contains(i))).collect(Collectors.toList());
    }

    public boolean checkRequestedDate(String startDate,
                                      String endDate) {

        LocalDate startdate, enddate;

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startdate = LocalDate.parse(startDate, formatter);
            enddate = LocalDate.parse(endDate, formatter);
        } catch(DateTimeParseException e){
            return false;
        }

        if (startdate.isAfter(enddate) || startdate.isEqual(enddate))
            return false;

        if (startdate.isBefore(LocalDate.now(clock)))
            return false;

        return true;
    }

    public boolean checkRequestedAvailability(Request request) {
        return transactionService.itemIsAvailableOnTime(request);
    }

    public boolean checkRequesterBalance(Item item, String username) {
        return proPaySubscriber.checkDeposit(item.getDeposit(), username);
    }

    public Request addBuyRequest(Long id) {

        Person currentUser = authenticationService.getCurrentUser();
        Item item = itemRepo.findById(id).orElse(null);

        String username = currentUser.getUsername();

        if (!checkRequesterBalance(item, username))
            return null;

        Request request = new Request();
        request.setRequester(personsRepo.findByUsername(currentUser.getUsername()));
        request.setStatus(AWAITING_SHIPMENT);
        request.setRequestedItem(item);
        request.setItemName(item.getName());
        request.setOwnerName(username);

        return request;
    }

    public boolean buyItemAndTransferMoney(Request request){
        Person currentUser = authenticationService.getCurrentUser();
        String username = currentUser.getUsername();
        Item item = request.getRequestedItem();

        if (!proPaySubscriber
                .transferMoney(
                        username,
                        item.getOwner().getUsername(),
                        item.getRetailPrice())) {
            return false;
        }

        item.setActive(false);
        requestRepo.save(request);
        return true;
    }

    public Request addRequest(String startDate, String endDate, Long id) {

        if(!checkRequestedDate(startDate, endDate)){
            return null;
        }

        LocalDate startdate, enddate;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startdate = LocalDate.parse(startDate, formatter);
        enddate = LocalDate.parse(endDate, formatter);

        Person currentUser = authenticationService.getCurrentUser();
        Item item = itemRepo.findById(id).orElse(null);

        Request request = new Request();
        request.setRequester(personsRepo.findByUsername(currentUser.getUsername()));
        request.setStartDate(startdate);
        request.setEndDate(enddate);
        request.setDuration(Period.between(startdate, enddate).getDays());
        request.setRequestedItem(item);
        request.setItemName(item.getName());
        request.setOwnerName(currentUser.getUsername());

        return request;
    }

    public boolean saveRequest(Request request){
        Person currentUser = authenticationService.getCurrentUser();
        String username = currentUser.getUsername();

        if (!checkRequestedAvailability(request) ||
                !checkRequesterBalance(request.getRequestedItem(), username)){
            return false;
        }
        requestRepo.save(request);
        return true;
    }

    public boolean wasShipped(Request request, Integer shipped){
        if (shipped != null) {
            request.setStatus(RequestStatus.SHIPPED);
            return true;
        }
        return false;
    }

    public boolean wasDeniedOrAccepted(Integer requestMyItems, Request request){
        if(requestMyItems == -1){
            request.setStatus(RequestStatus.DENIED);
            requestRepo.save(request);
            return true;
        }
        return transactionService.lenderApproved(request);
    }


}
