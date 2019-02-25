package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static Bendispository.Abschlussprojekt.model.RequestStatus.*;

@Component
public class RequestService {

    PersonsRepo personsRepo;

    RequestRepo requestRepo;

    private final AuthenticationService authenticationService;

    private final ItemRepo itemRepo;

    private final LeaseTransactionRepo leaseTransactionRepo;

    private final ProPaySubscriber proPaySubscriber;

    private final TransactionService transactionService;

    private final PaymentTransactionRepo paymentTransactionRepo;

    private final ConflictTransactionRepo conflictTransactionRepo;


    @Autowired
    public RequestService(PersonsRepo personsRepo,
                          RequestRepo requestRepo,
                          ItemRepo itemRepo,
                          LeaseTransactionRepo leaseTransactionRepo,
                          PaymentTransactionRepo paymentTransactionRepo,
                          ConflictTransactionRepo conflictTransactionRepo,
                          RatingRepo ratingRepo){
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.paymentTransactionRepo = paymentTransactionRepo;
        this.personsRepo = personsRepo;
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.authenticationService = new AuthenticationService(personsRepo);
        this.proPaySubscriber = new ProPaySubscriber(personsRepo,
                leaseTransactionRepo);
        this.transactionService = new TransactionService(leaseTransactionRepo,
                requestRepo,
                proPaySubscriber,
                paymentTransactionRepo,
                conflictTransactionRepo,
                ratingRepo);
    }

    public void showRequests(Model model,
                              Long id) {
        Person me = personsRepo.findById(id).orElse(null);
        List<Request> myRequests = requestRepo.findByRequesterAndStatus(me, PENDING);
        myRequests.addAll(requestRepo.findByRequesterAndStatus(me, DENIED));
        deleteObsoleteRequests(myRequests);
        model.addAttribute("myRequests", myRequests);

        List<Request> requestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, PENDING);
        deleteObsoleteRequests(requestsMyItems);
        model.addAttribute("requestsMyItems", requestsMyItems);

        List<Request> myBuyRequests = requestRepo.findByRequesterAndStatus(me, PENDINGSELL);
        deleteObsoleteRequests(myBuyRequests);
        model.addAttribute("myBuyRequests", myBuyRequests);

        List<Request> buyRequestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, PENDINGSELL);
        deleteObsoleteRequests(buyRequestsMyItems);
        model.addAttribute("buyRequestsMyItems", buyRequestsMyItems);

    }

    private void deleteObsoleteRequests(List<Request> myRequests) {
        List<Request> toRemove = new ArrayList<>();
        for(Request request : myRequests){
            if(request.getStartDate().isBefore(LocalDate.now())) {
                requestRepo.delete(request);
                toRemove.add(request);
            }
        }
        myRequests.removeAll(toRemove);
    }

    public boolean checkRequestedDate(String startDate,
                                      String endDate,
                                      RedirectAttributes redirectAttributes) {

        LocalDate startdate, enddate;

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startdate = LocalDate.parse(startDate, formatter);
            enddate = LocalDate.parse(endDate, formatter);
        } catch(DateTimeParseException e){
            redirectAttributes.addFlashAttribute("message",
                    "Invalid date!");
            return false;
        }

        if (startdate.isAfter(enddate) || startdate.isEqual(enddate)) {
            redirectAttributes.addFlashAttribute("message",
                    "Invalid date!");
            //return "redirect:/item{id}/requestItem";
            return false;
        }

        if (startdate.isBefore(LocalDate.now())){
            redirectAttributes.addFlashAttribute("message",
                    "Start date can't be in the past!");
            return false;
        }

        return true;
    }

    public boolean checkRequestedAvailability(RedirectAttributes redirectAttributes,
                                              Request request) {

        if (!transactionService.itemIsAvailableOnTime(request)) {
            redirectAttributes.addFlashAttribute("message",
                    "Item is not available during selected period!");
            return false;
        }
        return true;
    }

    public boolean checkRequesterBalance(RedirectAttributes redirectAttributes,
                                         Item item,
                                         String username) {
        if (!proPaySubscriber.checkDeposit(item.getDeposit(), username)) {
            redirectAttributes.addFlashAttribute("messageBalance",
                    "You don't have enough funds for this transaction!");
            return false;
        }
        return true;
    }

    public String addBuyRequest(Model model,
                                RedirectAttributes redirectAttributes,
                                @PathVariable Long id) {

        Person currentUser = authenticationService.getCurrentUser();
        Item item = itemRepo.findById(id).orElse(null);

        Request request = new Request();
        request.setRequester(personsRepo.findByUsername(currentUser.getUsername()));
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(1));
        request.setDuration(0);
        request.setStatus(PENDINGSELL);
        request.setRequestedItem(item);

        String username = currentUser.getUsername();

        if (!checkRequesterBalance(redirectAttributes, item, username))
            return "redirect:/item/{id}";

        requestRepo.save(request);
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        redirectAttributes.addFlashAttribute("success", "Buy request has been sent!");

        return "redirect:/item/{id}";

    }

    public String addRequest(Model model,
                           RedirectAttributes redirectAttributes,
                           String startDate,
                           String endDate,
                           @PathVariable Long id) {

        if (!checkRequestedDate(startDate, endDate, redirectAttributes))
            return "redirect:/item/{id}/requestitem";

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
        String username = currentUser.getUsername();

        if (!checkRequestedAvailability(redirectAttributes, request) ||
                !checkRequesterBalance(redirectAttributes, item, username))
            return "redirect:/item/{id}/requestitem";

        requestRepo.save(request);
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        redirectAttributes.addFlashAttribute("success", "Request has been sent!");

        return "redirect:/item/{id}";
    }
}
