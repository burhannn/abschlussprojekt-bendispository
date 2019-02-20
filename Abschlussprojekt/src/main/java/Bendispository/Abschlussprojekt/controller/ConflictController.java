package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ConflictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


//

@Controller
public class ConflictController {

    private final AuthenticationService authenticationService;

    private final ConflictService conflictService;

    private final ConflictTransactionRepo conflictTransactionRepo;

    @Autowired
    public ConflictController(ConflictTransactionRepo conflictTransactionRepo,
                              ConflictService conflictService,
                              AuthenticationService authenticationService){
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.conflictService = conflictService;
        this.authenticationService = authenticationService;
    }

    @GetMapping(path = "/conflicts")
    public String listAllConflictTransaction(Model model){
        Person loggedin = authenticationService.getCurrentUser();
        if(loggedin.getUsername().equals("admin")){
            List<ConflictTransaction> allConflicts =
                    conflictTransactionRepo
                            .findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse();
            model.addAttribute("allConflicts", allConflicts);
            return "conflictTransaction";
        }
        return "redirect:/";
    }

    @PostMapping(path = "/conflicts")
    public String addChangesConflictTransaction(Model model,
                                                Long conflictId,
                                                int beneficiary){
        ConflictTransaction conflict = conflictTransactionRepo.findById(conflictId).orElse(null);
        conflictService.resolveConflict(conflict, conflictTransactionRepo, beneficiary == -1);
        List<ConflictTransaction> allConflicts = conflictTransactionRepo.findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse();
        model.addAttribute("allConflicts", allConflicts);
        return "conflictTransaction";
    }
}
