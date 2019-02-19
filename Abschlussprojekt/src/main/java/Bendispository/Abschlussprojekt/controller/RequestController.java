package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;

import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;

import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.time.Period;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static Bendispository.Abschlussprojekt.model.RequestStatus.PENDING;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class RequestController {

    private final RequestRepo requestRepo;

    private final ItemRepo itemRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private final PersonsRepo personsRepo;

    private final TransactionService transactionService;

    private final PaymentTransactionRepo paymentTransactionRepo;

    private final ProPaySubscriber proPaySubscriber;

    private final AuthenticationService authenticationService;

    @Autowired
    public RequestController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personsRepo,
                             PaymentTransactionRepo paymentTransactionRepo) {

        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personsRepo = personsRepo;
        this.paymentTransactionRepo = paymentTransactionRepo;

        this.authenticationService = new AuthenticationService(personsRepo);
        this.proPaySubscriber = new ProPaySubscriber(personsRepo,
                                                     leaseTransactionRepo);
        this.transactionService = new TransactionService(leaseTransactionRepo,
                                                         requestRepo,
                                                         proPaySubscriber,
                                                         paymentTransactionRepo);
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
                                     @PathVariable Long id,
                                     RedirectAttributes redirectAttributes
                                     //@RequestParam("startDay")
                                     ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startdate = LocalDate.parse(startDate, formatter);
        LocalDate enddate = LocalDate.parse(endDate, formatter);


        Person currentUser = authenticationService.getCurrentUser();
        Item item = itemRepo.findById(id).orElse(null);

        Request request = new Request();
        request.setRequester(personsRepo.findByUsername(currentUser.getUsername())); ///// änderung
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

        if (!proPaySubscriber.checkDeposit(item.getDeposit(), username))
            redirectAttributes.addFlashAttribute("message", "You don't have enough money for the deposit!");

        if (!transactionService.itemIsAvailableOnTime(request))
            redirectAttributes.addFlashAttribute("message", "Item is not available during selected period!");

        return "redirect:/item{id}/requestItem";
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
        Long id = authenticationService.getCurrentUser().getId();

        if(requestMyItems == -1){
            request.setStatus(RequestStatus.DENIED);
            requestRepo.save(request);
            showRequests(model,id);
            return "requests";
        }
        if(transactionService.lenderApproved(request)){
            showRequests(model,id);
            return "requests";
        }
        showRequests(model,id);
        return "request_reservation_not_possible";
    }

    @GetMapping(path="/profile/rentedItems")
    public String rentedItems(Model model){
        Person me = authenticationService.getCurrentUser();
        List<LeaseTransaction> myRentedItems = leaseTransactionRepo.findAllByLeaserAndItemIsReturnedIsFalse(me);
        model.addAttribute("myRentedItems", myRentedItems);
        return "rentedItems";
    }

    @PostMapping(path = "/profile/rentedItems")
    public String returnItem(Model model,
                             Long id){
        LeaseTransaction leaseTransaction = leaseTransactionRepo.findById(id).orElse(null);
        System.out.println(leaseTransaction.getLeaser().getUsername());
        transactionService.itemReturnedToLender(leaseTransaction);
        return "rentedItems";
    }

    private void showRequests(Model model,
                              Long id) {
        Person me = personsRepo.findById(id).orElse(null);
        List<Request> listMyRequests = requestRepo.findByRequester(me);
        model.addAttribute("myRequests", listMyRequests);
        List<Request> RequestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, PENDING);
        model.addAttribute("requestsMyItems", RequestsMyItems);
    }
}

