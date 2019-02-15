package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.time.Period;

import java.time.LocalDate;

import static Bendispository.Abschlussprojekt.service.ProPaySubscriber.*;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RequestController {
    
    final RequestRepo requestRepo;

    final ItemRepo itemRepo;

    final LeaseTransactionRepo leaseTransactionRepo;

    TransactionService transactionService;

    ProPaySubscriber proPaySubscriber;

    @Autowired
    public RequestController(RequestRepo requestRepo, ItemRepo itemRepo, LeaseTransactionRepo leaseTransactionRepo) {
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
    }

    @GetMapping(path = "/item{id}/requestItems")
    public String request(Model model, @PathVariable Long id){
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        return "formRequest";
    }

    @PostMapping(path = "/item{id}/requestItem")
    public String addRequestToLender(@ModelAttribute("request") Request request,
                                     Model model,
                                     @PathVariable Long id
                                     //@RequestParam("startDay")
                                     ){
        model.addAttribute("newRequest", request);
        requestRepo.save(request);
        String username = "";
        Item item = itemRepo.findById(id).orElse(null);
        if(proPaySubscriber.checkDeposit(item.getDeposit(), username)
                && transactionService.itemIsAvailableOnTime(request)){

            /*

            Kaution reicht aus, wird "abgeschickt" (erstellt und gespeichert)

            request.setRequestedItem(item);
            LocalDate startDate = LocalDate.of(1,1,1), endDate = LocalDate.of(2,1,1);
            request.setDuration(Period.between(startDate, endDate).getDays());
            requestRepo.save(request);
            itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));

            return "formRequest";
            */


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

    @PostMapping(path = "/item{id}/requestItemsssss")
    public String requestDenied(@ModelAttribute("request") Request request,
                                     Model model,
                                     @PathVariable Long id){

        return "";
    }
}

