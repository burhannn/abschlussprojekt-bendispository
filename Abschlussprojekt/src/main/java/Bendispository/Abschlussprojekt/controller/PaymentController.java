package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.ServiceTests.AuthenticationService;
import Bendispository.Abschlussprojekt.ServiceTests.ProPaySubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PaymentController {

    private AuthenticationService authenticationService;

    private ProPaySubscriber proPaySubscriber;

    @Autowired
    public PaymentController(PersonsRepo personsRepo,
                             LeaseTransactionRepo leaseTransactionRepo) {
        this.authenticationService = new AuthenticationService(personsRepo);
        this.proPaySubscriber = new ProPaySubscriber(personsRepo,
                leaseTransactionRepo);
    }

    @GetMapping(path = "/profile/paymenttransaction")
    public String concludeTransaction(){
        return "/";
    }

    @GetMapping(path = "/chargeaccount")
    public String saveAccount(Model model,
                              RedirectAttributes redirectAttributes){
        Person currentUser = authenticationService.getCurrentUser();
        String username = currentUser.getUsername();
        ProPayAccount proPayAccount = proPaySubscriber.getAccount(username);
        if(proPayAccount == null) {
            proPayAccount = new ProPayAccount();
            model.addAttribute("message", "Something went wrong with ProPay!");
        }
        model.addAttribute("person", currentUser);
        model.addAttribute("account", proPayAccount);
        return "rentsTmpl/chargeAccount";
    }

    @PostMapping(path = "/chargeaccount")
    public String chargeAccount(Model model,
                                RedirectAttributes redirectAttributes,
                                double amount) {

        if (amount < 0) {
            redirectAttributes.addFlashAttribute("message", "Amount can't be negative!");
            return "redirect:/chargeaccount";
        }

        Person currentUser = authenticationService.getCurrentUser();
        String username = currentUser.getUsername();
        ProPayAccount account = proPaySubscriber.chargeAccount(username, amount);
        if(account == null){
            redirectAttributes.addFlashAttribute("message", "Something went wrong with ProPay!");
            model.addAttribute("person", currentUser);
            return "redirect:/chargeaccount";
        }

        account = proPaySubscriber.getAccount(username);
        if(account == null){
            redirectAttributes.addFlashAttribute("message", "Something went wrong with ProPay!");
            model.addAttribute("person", currentUser);
            return "redirect:/chargeaccount";
        }
        model.addAttribute("success", "Account has been charged!");
        model.addAttribute("person", currentUser);
        model.addAttribute("account", account);
        return "rentsTmpl/chargeAccount";
    }

}
