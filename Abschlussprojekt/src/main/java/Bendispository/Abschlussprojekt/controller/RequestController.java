package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.*;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.RequestService;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static Bendispository.Abschlussprojekt.model.RequestStatus.AWAITING_SHIPMENT;
import static Bendispository.Abschlussprojekt.model.RequestStatus.DENIED;
import static Bendispository.Abschlussprojekt.model.RequestStatus.PENDING;

@Controller
public class RequestController {

    private final RequestRepo requestRepo;
    private final ItemRepo itemRepo;
    private final LeaseTransactionRepo leaseTransactionRepo;
    private final PersonsRepo personsRepo;
    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;
    private final RequestService requestService;
    private Clock clock;

    @Autowired
    public RequestController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personsRepo,
                             RequestService requestService,
                             Clock clock,
                             AuthenticationService authenticationService,
                             TransactionService transactionService) {
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personsRepo = personsRepo;
        this.authenticationService = authenticationService;
        this.transactionService = transactionService;
        this.clock = clock;
        this.requestService = requestService;
    }

    @GetMapping(path = "/item/{id}/requestitem")
    public String request(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        if (itemRepo.findById(id).get().getOwner()
                .equals(authenticationService.getCurrentUser())){
            return "redirect:/item/{id}";
        }
        List<Request> requests = requestRepo.findByRequesterAndRequestedItemAndStatus
                (authenticationService.getCurrentUser(), itemRepo.findById(id).get(), RequestStatus.PENDING);
        if (!(requests.isEmpty())) {
            redirectAttributes.addFlashAttribute("message",
                    "You cannot request the same item twice!");
            return "redirect:/item/{id}";
        }

        List <LeaseTransaction> list = leaseTransactionRepo
                                         .findAllByItemIdAndEndDateGreaterThan(id, LocalDate.now(clock));

        Collections.sort(list, Comparator.comparing(LeaseTransaction::getStartDate));
        model.addAttribute("leases", list);
        return "rentsTmpl/formRequest";
    }

    @PostMapping(path = "/item/{id}/requestitem")
    public String addRequestToLender(String startDate,
                                     String endDate,
                                     Model model,
                                     @PathVariable Long id,
                                     RedirectAttributes redirectAttributes
                                     ){

        Request request = requestService
                .addRequest(startDate, endDate, id);
        if(request == null){
            redirectAttributes.addFlashAttribute("message",
                    "Invalid date!");
            return "redirect:/item/{id}/requestitem";
        }

        boolean saveRequest = requestService.saveRequest(request);

        if(!saveRequest){
            redirectAttributes.addFlashAttribute("message",
                    "Item is not available during selected period, or something went wrong with ProPay!");
        }

        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        redirectAttributes.addFlashAttribute("success", "Request has been sent!");

        return "redirect:/item/{id}";
    }

    @GetMapping(path="/profile/requests")
    public String Requests(Model model){
        Person person = authenticationService.getCurrentUser();
        showRequests(model, person);
        return "rentsTmpl/requests";
    }

    public void showRequests(Model model,
                             Person me) {
        List<Request> myRequests = requestRepo.findByRequesterAndStatus(me, PENDING);
        myRequests.addAll(requestRepo.findByRequesterAndStatus(me, DENIED));
        myRequests = requestService.deleteObsoleteRequests(myRequests);
        model.addAttribute("myRequests", myRequests);

        List<Request> requestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, PENDING);
        requestsMyItems = requestService.deleteObsoleteRequests(requestsMyItems);
        model.addAttribute("requestsMyItems", requestsMyItems);

        List<Request> myBuyRequests = requestRepo.findByRequesterAndStatus(me, AWAITING_SHIPMENT);
        model.addAttribute("myBuyRequests", myBuyRequests);

        List<Request> buyRequestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, AWAITING_SHIPMENT);
        model.addAttribute("buyRequestsMyItems", buyRequestsMyItems);

    }

    @PostMapping(path = "/profile/deleterequest/{id}")
    public String deleteRequest(@PathVariable Long id){
        requestRepo.deleteById(id);
        return "redirect:/";
    }

    @PostMapping(path="/profile/requests")
    public String AcceptDeclineRequests(Model model,
                                        Long requestID,
                                        Integer requestMyItems,
                                        Integer shipped,
                                        RedirectAttributes redirectAttributes){
        Request request = requestRepo.findById(requestID).orElse(null);
        Person person = authenticationService.getCurrentUser();

        if (shipped != null) {
            if (shipped == 1) {
                request.setStatus(RequestStatus.SHIPPED);
                showRequests(model, person);
                return "rentsTmpl/requests";
            }
        }

        if (requestMyItems != null) {
            if(requestMyItems == -1){
                request.setStatus(RequestStatus.DENIED);
                requestRepo.save(request);
                showRequests(model, person);
                return "rentsTmpl/requests";
            } else {
                if (transactionService.lenderApproved(request)) {
                    showRequests(model, person);
                    return "rentsTmpl/requests";
                }
            }
        }

        showRequests(model,person);
        redirectAttributes.addFlashAttribute("message", "Funds not sufficient for deposit or something else went wrong!");
        return "redirect:/";
    }

    @GetMapping(path="/profile/renteditems")
    public String rentedItems(Model model){
        showRentedAndLeasedItems(model);
        return "itemTmpl/rentedItems";
    }

    public void showRentedAndLeasedItems(Model model){
        Person me = authenticationService.getCurrentUser();
        List<LeaseTransaction> myRentedItems = leaseTransactionRepo.findAllByLeaserAndItemIsReturnedIsFalse(me);
        model.addAttribute("myRentedItems", myRentedItems);
        List<LeaseTransaction> myLeasedItems = leaseTransactionRepo.findAllByItemOwnerAndItemIsReturnedIsFalse(me);
        model.addAttribute("myLeasedItems", myLeasedItems);
    }

    @PostMapping(path = "/profile/renteditems")
    public String returnItem(Model model,
                             Long id,
                             RedirectAttributes redirectAttributes){
        LeaseTransaction leaseTransaction = leaseTransactionRepo.findById(id).orElse(null);
        if( !(transactionService.itemReturnedToLender(leaseTransaction))){
            redirectAttributes.addFlashAttribute("message", "Something wrong with ProPay!");
            showRentedAndLeasedItems(model);
            return "redirect:/profile/renteditems";
        }
        showRentedAndLeasedItems(model);
        return "itemTmpl/rentedItems";
    }

    @GetMapping(path= "/profile/returneditems")
    public String returnedItem(Model model){
        Person me = authenticationService.getCurrentUser();
        List<LeaseTransaction> transactionList =
                leaseTransactionRepo
                        .findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(me);
        model.addAttribute("transactionList", transactionList);
        return "itemTmpl/returnedItems";
    }

    @PostMapping(path= "/profile/returneditems")
    public String stateOfItem(Model model,
                              Long transactionId,
                              Integer itemIntact,
                              RedirectAttributes redirectAttributes){
        LeaseTransaction leaseTransaction = leaseTransactionRepo
                                                    .findById(transactionId)
                                                    .orElse(null);
        Long id = authenticationService.getCurrentUser().getId();
        Person me = personsRepo.findById(id).orElse(null);
        if(itemIntact == -1){
            return "redirect:/profile/returneditems/" + transactionId + "/issue";
        }
        if(!(transactionService.itemIsIntact(leaseTransaction))){
            redirectAttributes.addFlashAttribute("message", "Something went wrong with ProPay!");
            model.addAttribute("transactionList", leaseTransactionRepo.findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(me));
            return "redirect:/profile/returneditems";
        }
        List<LeaseTransaction> transactionList =
                leaseTransactionRepo
                        .findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(me);
        model.addAttribute("transactionList", transactionList);
        return "itemTmpl/returnedItems";
    }

    @GetMapping(path= "/profile/returneditems/{transactionId}/issue")
    public String returnedItemIsNotIntact(Model model,
                                          @PathVariable Long transactionId){
        LeaseTransaction transaction = leaseTransactionRepo.findById(transactionId).orElse(null);
        String comment = "";
        model.addAttribute("comment", comment);
        model.addAttribute("transaction", transaction);
        return "issue";
    }

    @PostMapping(path= "/profile/returneditems/{id}/issue")
    public String returnedItemIsNotIntactPost(Model model,
                                              @PathVariable Long id,
                                              String comment){
        Long userId = authenticationService.getCurrentUser().getId();
        Person me = personsRepo.findById(userId).orElse(null);
        LeaseTransaction leaseTransaction = leaseTransactionRepo
                .findById(id)
                .orElse(null);
        leaseTransaction.setLeaseIsConcluded(true);
        transactionService.itemIsNotIntact(me, leaseTransaction, comment);
        List<LeaseTransaction> transactionList =
              leaseTransactionRepo
                    .findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(me);
        model.addAttribute("transactionList", transactionList);
        return "itemTmpl/returnedItems";
    }
}

