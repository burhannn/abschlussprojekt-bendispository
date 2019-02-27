package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.*;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ProfilController {

    private final RequestRepo requestRepo;
    private final ItemRepo itemRepo;
    private final LeaseTransactionRepo leaseTransactionRepo;
    private final PersonsRepo personRepo;
    private final TransactionService transactionService;
    private final ProPaySubscriber proPaySubscriber;
    private final AuthenticationService authenticationService;
    private final RatingRepo ratingRepo;

    @Autowired
    public ProfilController(RequestRepo requestRepo,
                             ItemRepo itemRepo,
                             LeaseTransactionRepo leaseTransactionRepo,
                             PersonsRepo personRepo,
                             RatingRepo ratingrepo,
                            AuthenticationService authenticationService,
                            ProPaySubscriber proPaySubscriber,
                            TransactionService transactionService) {
        this.ratingRepo = ratingrepo;
        this.requestRepo = requestRepo;
        this.itemRepo = itemRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personRepo = personRepo;
        this.authenticationService = authenticationService;
        this.proPaySubscriber = proPaySubscriber;
        this.transactionService = transactionService;
    }

    @GetMapping(path= "/")
    public String Overview(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        for(LeaseTransaction leaseTransaction : leaseTransactionRepo.findAllByLeaserAndItemIsReturnedIsFalse(loggedIn)){
            if(transactionService.isTimeViolation(leaseTransaction)){
                model.addAttribute("message",
                        "You have to return an Item!");
                model.addAttribute("itemname", leaseTransaction.getItem().getName());
            }
        }
        List<Item> allOtherItems = itemRepo.findByOwnerNotAndActiveTrue(loggedIn);
        model.addAttribute("OverviewAllItems", allOtherItems);
        model.addAttribute("loggedInPerson",loggedIn);
        return "OverviewAllItems";
    }

    @GetMapping(path= "/profile")
    public String profile(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person", loggedIn);

        ProPayAccount account = proPaySubscriber.getAccount(loggedIn.getUsername());
        if(account == null){
            account = new ProPayAccount();
            model.addAttribute("message", "Something went wrong with ProPay!");
        }
        model.addAttribute("account", account);
        model.addAttribute("reservations", account.getReservations());
        return "profileTmpl/profile";
    }

    @GetMapping(path = "/openratings")
    public String openRatings(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        List<Rating> ratings = ratingRepo.findAllByRater(loggedIn);
        model.addAttribute("openRatings", ratings);
        return "profileTmpl/openRatings";
    }

    @PostMapping(path="/rating")
    public String Rating(Model model, @RequestParam("rating") int rating, @RequestParam("ratingID") Long ratingID){
        if (rating != -1){
            Rating rating1 = ratingRepo.findById(ratingID).orElse(null);
            rating1.setRatingPoints(rating);
            ratingRepo.save(rating1);
            if(authenticationService.getCurrentUser().getId().equals(rating1.getRequest().getRequestedItem().getOwner().getId())){
                rating1.getRequest().getRequester().addRating(rating1);
                personRepo.save(rating1.getRequest().getRequester());
            }else{
                rating1.getRequest().getRequestedItem().getOwner().addRating(rating1);
                personRepo.save(rating1.getRequest().getRequestedItem().getOwner());
            }
            model.addAttribute("rating", rating1);
        }

        return "redirect:/";
    }

    @GetMapping(path = "/profile/history")
    public String history(Model model){
        Person loggedIn = authenticationService.getCurrentUser();

        List<Request> purchases = requestRepo.findByRequesterAndStatus(loggedIn, RequestStatus.SHIPPED);
        List<Request> sales = requestRepo.findByRequestedItemOwnerAndStatus(loggedIn, RequestStatus.SHIPPED);

        List<LeaseTransaction> leased =
                leaseTransactionRepo
                        .findAllByLeaserAndLeaseIsConcludedIsTrue(loggedIn);
        List<LeaseTransaction> lent =
                leaseTransactionRepo
                        .findAllByItemOwnerAndLeaseIsConcludedIsTrue(loggedIn);

        model.addAttribute("purchases", purchases);
        model.addAttribute("sales", sales);
        model.addAttribute("leased", leased);
        model.addAttribute("lent", lent);
        return "historia";
    }


    @GetMapping(path= "/profile/{id}")
    public String profileOther(Model model,
                               @PathVariable Long id){
        Optional<Person> person = personRepo.findById(id);
        personRepo.findById(id).ifPresent(o -> model.addAttribute("person",o));
        return "profileTmpl/profileOther";
    }

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAllByUsernameNotAndUsernameNot(
                authenticationService.getCurrentUser().getUsername(),"admin");
        model.addAttribute("personen", all);
        model.addAttribute("loggedInPerson", authenticationService.getCurrentUser());
        return "profileTmpl/profileDetails";
    }

    @GetMapping(value = "/deleteuser/{username}")
    public String deleteUser(@PathVariable String username){
        if(authenticationService.getCurrentUser().getUsername().equals("admin")){
            Person deletePerson = personRepo.findByUsername(username);
            personRepo.delete(deletePerson);
            return "redirect:/profilub";
        }
        return "redirect:/";
    }

    @GetMapping(path= "/editprofile")
    public String editProfil(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person",loggedIn);

        return "profileTmpl/editProfile";
    }

    @PostMapping(path = "/editprofile")
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
