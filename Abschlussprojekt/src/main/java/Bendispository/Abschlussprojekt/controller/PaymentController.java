package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PaymentController {

    private PaymentTransactionRepo paymentTransactionRepo;

    private AuthenticationService authenticationService;

    private LeaseTransactionRepo leaseTransactionRepo;

    private PersonsRepo personsRepo;

    private ProPaySubscriber proPaySubscriber;

    @Autowired
    public PaymentController(PersonsRepo personsRepo,
                             LeaseTransactionRepo leaseTransactionRepo) {

        this.leaseTransactionRepo = leaseTransactionRepo;
        this.personsRepo = personsRepo;

        this.authenticationService = new AuthenticationService(personsRepo);
        this.proPaySubscriber = new ProPaySubscriber(personsRepo,
                leaseTransactionRepo);
    }

    @GetMapping(path = "/profile/PaymentTransaction")
    public String concludeTransaction(Model model){
        return "/";
    }

    @GetMapping(path = "/chargeAccount")
    public String saveAccount(){
        return "/chargeAccount";
    }

    @PostMapping(path="/chargeAccount")
    public String chargeAccount(double amount) {
        Person currentUser = authenticationService.getCurrentUser();
        String username = currentUser.getUsername();

        ProPaySubscriber proPaySubscriber = new ProPaySubscriber(personsRepo, leaseTransactionRepo);

        proPaySubscriber.chargeAccount(username, amount);

        return "chargeAccount";
    }
}
