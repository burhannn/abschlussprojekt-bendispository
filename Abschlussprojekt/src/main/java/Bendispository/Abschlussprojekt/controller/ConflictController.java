package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
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

    private ConflictService conflictService;

    private final ConflictTransactionRepo conflictTransactionRepo;

    @Autowired
    public ConflictController(ConflictTransactionRepo conflictTransactionRepo,
                              ConflictService conflictService){
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.conflictService = conflictService;
    }

    @GetMapping(path = "/profile/conflicts")
    public String listAllConflictTransaction(Model model){
        List<ConflictTransaction> allConflicts = conflictTransactionRepo.findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse();
        model.addAttribute("allConflicts", allConflicts);
        return "conflictTransaction";
    }

    @PostMapping(path = "/profile/conflicts")
    public String addChangesConflictTransaction(Model model,
                                                Long conflictId,
                                                int benificiary){
        ConflictTransaction conflict = conflictTransactionRepo.findById(conflictId).orElse(null);
        conflictService.resolveConflict(conflict, conflictTransactionRepo, benificiary == -1);
        List<ConflictTransaction> allConflicts = conflictTransactionRepo.findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse();
        model.addAttribute("allConflicts", allConflicts);
        return "conflictTransaction";
    }
}
