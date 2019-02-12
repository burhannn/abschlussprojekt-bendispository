/*package Bendispository.Abschlussprojekt.web;

import Bendispository.Abschlussprojekt.model.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.model.RequestRepo;
import Bendispository.Abschlussprojekt.repo.ConcludeTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

public class ControllerTransactions {

    @Autowired
    ConcludeTransactionRepo concludeTransactionRepo;

    @Autowired
    LeaseTransactionRepo leaseTransactionRepo;

    @Autowired
    RequestRepo requestRepo;

    @GetMapping(path = "/item+{id}/request"){
        public String request(Model model){
            return "request";
        }
    }

    @GetMapping(path = "/profile/leaseTransaction"){
        public String leaseTransaction(Model model){
            return "leaseTransaction";
        }
    }

    @GetMapping(path = "/profile/PaymentTransaction"){
        public String concludeTransaction(Model model){
            return "/";
        }
    }


}
*/