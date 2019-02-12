package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.repo.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LeaseController {

    @Autowired
    LeaseTransactionRepo leaseTransactionRepo;

    @GetMapping(path = "/profile/leaseTransaction"){
        public String leaseTransaction(Model model){
            return "leaseTransaction";
        }
    }
}
