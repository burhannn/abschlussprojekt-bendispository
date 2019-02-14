package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.transactionModels.ConcludeTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConcludeTransactionRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

// transaktion hat geklappt oder nicht

@Controller
public class ConcludeController {

    @Autowired
    ConcludeTransactionRepo concludeTransactionRepo;

    @GetMapping(path = "/profile/concludeTransaction")
    public String listAllConcludeTransaction(Model model){
        List<ConcludeTransaction> allConclude = concludeTransactionRepo.findAll();
        model.addAttribute("allConclude", allConclude);
        return "concludeTransaction";
    }

    @GetMapping(path = "/profile/concludeTransaction{id}")
    public String showTransactionById(Model model, @PathVariable Long id){
        Optional<ConcludeTransaction> conclude = concludeTransactionRepo.findById(id);
        model.addAttribute("conclude", conclude.get());
        return "concludeTransaction";
    }

    @PostMapping(path = "/profile/concludeTransaction{id}")
    public String addChangesConcludeTransaction(Model model, @PathVariable Long id, ConcludeTransaction concludeTransaction){
        model.addAttribute("changeConclude", concludeTransaction);
        concludeTransactionRepo.save(concludeTransaction);
        return "concludeTransaction";
    }
}
