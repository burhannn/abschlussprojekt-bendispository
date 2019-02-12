package Bendispository.Abschlussprojekt.Controller;

import Bendispository.Abschlussprojekt.Model.LeaseTransaction;
import Bendispository.Abschlussprojekt.Repo.LeaseTransactionRepo;
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

    @GetMapping(path = "/profile/leaseTransaction")
    public String listAllLeaseTransaction(Model model){
        List<LeaseTransaction> allLease = leaseTransactionRepo.findAll();
        model.addAttribute("allLease", allLease);
        return "leaseTransaction";
    }

    @GetMapping(path = "/profile/leaseTransaction+{id}")
    public String showTransactionById(Model model, @PathVariable Long id){
        Optional<LeaseTransaction> lease = leaseTransactionRepo.findById(id);
        model.addAttribute("lease", lease.get());
        return "leaseTransaction";
    }

    @PostMapping(path = "/profile/leaseTransaction+{id}")
    public String addChangesLeaseTransaction(Model model, @PathVariable Long id, LeaseTransaction leaseTransaction){
        model.addAttribute("changeLease", leaseTransaction);
        leaseTransactionRepo.save(leaseTransaction);
        return "leaseTransaction";
    }

}