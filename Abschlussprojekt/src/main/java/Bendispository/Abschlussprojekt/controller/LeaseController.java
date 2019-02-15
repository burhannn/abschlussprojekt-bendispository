package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class LeaseController {

    @Autowired
    LeaseTransactionRepo leaseTransactionRepo;

    

    @PostMapping(path = "/profile/leaseTransaction{id}")
    public String addChangesLeaseTransaction(Model model, @PathVariable Long id, LeaseTransaction leaseTransaction){
        model.addAttribute("changeLease", leaseTransaction);
        leaseTransactionRepo.save(leaseTransaction);
        return "requests";
    }

}