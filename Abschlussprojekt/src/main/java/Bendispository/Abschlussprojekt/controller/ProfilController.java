package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.model.Request;
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
import Bendispository.Abschlussprojekt.service.RequestService;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final RequestService requestService;
    private final RatingRepo ratingRepo;
    private final ConflictTransactionRepo conflictTransactionRepo;

    @Autowired
    public ProfilController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personRepo,
                             PaymentTransactionRepo paymentTransactionRepo,
                             RatingRepo ratingrepo,
                             ConflictTransactionRepo conflictTransactionRepo,
                             RequestService requestService) {
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
                conflictTransactionRepo,
                ratingRepo);
        this.requestService = requestService;
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
    @GetMapping(path = "/openRatings")
    public String openRatings(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        List<Rating> ratings = ratingRepo.findAllByRater(loggedIn);
        model.addAttribute("openRatings", ratings);
        return "openRatings";
    }

    @PostMapping(path="/rating")
    public String Rating(Model model,
                         int rating,
                         Long ratingID){
        if (rating != -1){
        Rating rating1 = ratingRepo.findById(ratingID).orElse(null);
        rating1.setRatingPoints(rating);
        ratingRepo.save(rating1);

        if(authenticationService.getCurrentUser().getId() == rating1.getRequest().getRequestedItem().getOwner().getId()){
            rating1.getRequest().getRequester().addRating(rating1);
            personRepo.save(rating1.getRequest().getRequester());
        }else{
            rating1.getRequest().getRequestedItem().getOwner().addRating(rating1);
            personRepo.save(rating1.getRequest().getRequestedItem().getOwner());
        }
        }
        return "redirect:";
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
        List<Person> all = personRepo.findAllByUsernameNotAndUsernameNot(
                                authenticationService.getCurrentUser().getUsername(),"admin");
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
    @GetMapping(path= "/editProfile")
    public String editProfil(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person",loggedIn);

        return "editProfile";
    }
    @PostMapping(path = "editProfile")
    public String saveProfileInDatabase(
            @RequestParam(value = "Firstname", required = true) String firstName,
            @RequestParam(value = "Lastname", required = true) String lastName,
            @RequestParam(value = "Password", required = true) String password,
            @RequestParam(value = "Email", required = true) String email,
            @RequestParam(value = "City", required = true) String city) {

        Person loggedIn = authenticationService.getCurrentUser();
        loggedIn.setFirstName(firstName);
        loggedIn.setLastName(lastName);
        loggedIn.setPassword(password);
        loggedIn.setEmail(email);
        loggedIn.setCity(city);
        personRepo.save(loggedIn);
        return "redirect:/profile";
    }
}
