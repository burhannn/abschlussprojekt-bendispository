package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProfilController {

    ItemRepo itemRepo;
    PersonsRepo personRepo;
    RequestRepo requestRepo;
    LeaseTransactionRepo leaseTransactionRepo;
    AuthenticationService authenticationService;

    @Autowired
    public ProfilController(ItemRepo itemRepo, PersonsRepo personsRepo, RequestRepo requestRepo,
                            LeaseTransactionRepo leaseTransactionRepo, AuthenticationService authenticationService){
        this.itemRepo = itemRepo;
        this.personRepo = personsRepo;
        this.requestRepo = requestRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.authenticationService = authenticationService;
    }

    @GetMapping(path= "/")
    public String Overview(Principal principal, Model model){
        Person loggedIn = authenticationService.getCurrentUser();
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
