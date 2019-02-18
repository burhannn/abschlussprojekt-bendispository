package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static Bendispository.Abschlussprojekt.model.RequestStatus.APPROVED;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RequestController {

    private final RequestRepo requestRepo;

    private final ItemRepo itemRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private final PersonsRepo personsRepo;

    private TransactionService transactionService;

    private ProPaySubscriber proPaySubscriber;

    private AuthenticationService authenticationService;

    @Autowired
    public RequestController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personsRepo) {
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personsRepo = personsRepo;
        this.authenticationService = new AuthenticationService(personsRepo);
        this.proPaySubscriber = new ProPaySubscriber(personsRepo, leaseTransactionRepo);
        this.transactionService = new TransactionService(leaseTransactionRepo, requestRepo);
    }

    @GetMapping(path = "/item{id}/requestItem")
    public String request(Model model, @PathVariable Long id){
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        return "formRequest";
    }

    @PostMapping(path = "/item{id}/requestItem")
    public String addRequestToLender(String startDate,
                                     String endDate,
                                     Model model,
                                     @PathVariable Long id
                                     //@RequestParam("startDay")
                                     ){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startdate = LocalDate.parse(startDate, formatter);
        LocalDate enddate = LocalDate.parse(endDate, formatter);

        Person currentUser = authenticationService.getCurrentUser();

        Item item = itemRepo.findById(id).orElse(null);

        Request request = new Request();
        request.setRequester(currentUser);
        request.setStartDate(startdate);
        request.setEndDate(enddate);
        request.setDuration(Period.between(startdate, enddate).getDays());
        request.setRequestedItem(item);
        String username = currentUser.getUsername();

        if(proPaySubscriber.checkDeposit(item.getDeposit(), username)
                && transactionService.itemIsAvailableOnTime(request)){

            //Kaution reicht aus, wird "abgeschickt" (erstellt und gespeichert)
            requestRepo.save(request);
            itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
            return "formRequest";
        }
        return "Could_not_send_Request";
    }

    @PostMapping(path = "/item{id}/requestItemsss")
    public String requestAccepted(@ModelAttribute("request") Request request,
                                Model model,
                                @PathVariable Long id){
        TransactionService transactionService = new TransactionService(leaseTransactionRepo, requestRepo);
        transactionService.lenderApproved(request);
        return "";
    }

    @GetMapping(path="/profile/requests")
    public String Requests(Model model){
        Long id = authenticationService.getCurrentUser().getId();
        showRequests(model,id);
        return "requests";
    }

    @PostMapping(path="/profile/requests")
    public String AcceptDeclineRequests(Model model,
                                        Long requestID,
                                        Integer requestMyItems){
        Request request = requestRepo.findById(requestID).orElse(null);
        request.setStatus(requestMyItems == -1 ? RequestStatus.DENIED : RequestStatus.APPROVED);
        requestRepo.save(request);
        Long id = authenticationService.getCurrentUser().getId();
        showRequests(model,id);
        return "requests";
    }

    @GetMapping(path="/profile/rentedItems")
    public String rentedItems(Model model){
        Long id = authenticationService.getCurrentUser().getId();
        Person me = personsRepo.findById(id).orElse(null);
        List<Request> myRentedItems = requestRepo.findByRequesterAndStatus(me, APPROVED);
        model.addAttribute("myRentedItems", myRentedItems);
        return "rentedItems";
    }

    private void showRequests(Model model,
                              Long id) {
        Person me = personsRepo.findById(id).orElse(null);
        List<Request> listMyRequests = requestRepo.findByRequester(me);
        model.addAttribute("myRequests", listMyRequests);
        List<Request> RequestsMyItems = requestRepo.findByRequestedItemOwner(me);
        model.addAttribute("requestsMyItems", RequestsMyItems);
    }
}

