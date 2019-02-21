package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ItemService {

    private final LeaseTransactionRepo leaseTransactionRepo;

    @Autowired
    public ItemService(LeaseTransactionRepo leaseTransactionRepo){
        this.leaseTransactionRepo = leaseTransactionRepo;
    }

    public boolean itemIsAvailable(Long id){
        List<LeaseTransaction> leaseTransactions = leaseTransactionRepo.findAllByItemId(id);
        LocalDate now = LocalDate.now();
        for (LeaseTransaction lease : leaseTransactions)
            if (TransactionService.isOverlapping(now, now, lease.getStartDate(), lease.getEndDate()))
                return false;
        return true;
    }

}
