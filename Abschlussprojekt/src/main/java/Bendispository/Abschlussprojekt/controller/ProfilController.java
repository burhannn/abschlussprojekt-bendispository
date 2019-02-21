package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfilController {

    private final RequestRepo requestRepo;
    private final ItemRepo itemRepo;
    private final LeaseTransactionRepo leaseTransactionRepo;
    private final PersonsRepo personRepo;
    private final TransactionService transactionService;
    private final PaymentTransactionRepo paymentTransactionRepo;
    private final ProPaySubscriber proPaySubscriber;
    private final AuthenticationService authenticationService;
    private RatingRepo ratingRepo;
    private final ConflictTransactionRepo conflictTransactionRepo;

    @Autowired
    public ProfilController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personRepo,
                             PaymentTransactionRepo paymentTransactionRepo,
                             RatingRepo ratingrepo,
                             ConflictTransactionRepo conflictTransactionRepo) {
        this.ratingRepo = ratingrepo;
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personRepo = personRepo;
        this.paymentTransactionRepo = paymentTransactionRepo;
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.authenticationService = new AuthenticationService(personRepo);
        this.proPaySubscriber = new ProPaySubscriber(personRepo,
                leaseTransactionRepo);
        this.transactionService = new TransactionService(leaseTransactionRepo,
                requestRepo,
                proPaySubscriber,
                paymentTransactionRepo,
                conflictTransactionRepo);
    }

    @GetMapping(path= "/")
    public String Overview(Principal principal, Model model, RedirectAttributes redirectAttributes){
        Person loggedIn = authenticationService.getCurrentUser();
        for(LeaseTransaction leaseTransaction : leaseTransactionRepo.findAllByLeaserAndItemIsReturnedIsFalse(loggedIn)){
            if(transactionService.isTimeViolation(leaseTransaction)){
                model.addAttribute("message",
                        "You have to return");
                model.addAttribute("itemname", leaseTransaction.getItem().getName());
            }
        }
        List<Item> allOtherItems = itemRepo.findByOwnerNot(personRepo.findByUsername(loggedIn.getUsername()));
        model.addAttribute("OverviewAllItems", allOtherItems);
        model.addAttribute("loggedInPerson",loggedIn);
        return "overviewAllItems";
    }

    @GetMapping(path= "/profile")
    public String profile(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person", loggedIn);

        ProPaySubscriber proPaySubscriber = new ProPaySubscriber(personRepo, leaseTransactionRepo);

        ProPayAccount proPayAccount = proPaySubscriber.getAccount(loggedIn.getUsername(), ProPayAccount.class);
        model.addAttribute("account", proPayAccount);
        model.addAttribute("reservations", proPayAccount.getReservations());
        return "profile";
    }

    @GetMapping(path = "/profile/history")
    public String history(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        List<LeaseTransaction> leased =
                leaseTransactionRepo
                        .findAllByLeaserAndLeaseIsConcludedIsTrue(loggedIn);
        List<LeaseTransaction> lent =
                leaseTransactionRepo
                        .findAllByItemOwnerAndLeaseIsConcludedIsTrue(loggedIn);
        model.addAttribute("leased", leased);
        model.addAttribute("lent", lent);
        return "historia";
    }

    @GetMapping(path= "/profile/{id}")
    public String profileOther(Model model,
                               @PathVariable Long id){
        Optional<Person> person = personRepo.findById(id);
        personRepo.findById(id).ifPresent(o -> model.addAttribute("person",o));
        return "profileOther";
    }

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAll();
        model.addAttribute("personen", all);
        model.addAttribute("loggedInPerson", authenticationService.getCurrentUser());
        return "profileDetails";
    }



    @GetMapping(value="deleteUser/{username}")
    public String deleteUser(@PathVariable String username){
        Person deletePerson = personRepo.findByUsername(username);
        personRepo.delete(deletePerson);
        return "redirect:/profilub";
    }
}
