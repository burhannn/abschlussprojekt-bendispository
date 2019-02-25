package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Component
public class ItemService {

    private final LeaseTransactionRepo leaseTransactionRepo;

    private Clock clock;

    @Autowired
    public ItemService(LeaseTransactionRepo leaseTransactionRepo, Clock clock){
        this.leaseTransactionRepo = leaseTransactionRepo;
        this.clock = clock;
    }

    public boolean itemIsAvailable(Long id){
        List<LeaseTransaction> leaseTransactions = leaseTransactionRepo.findAllByItemId(id);
        LocalDate now = LocalDate.now(clock);
        for (LeaseTransaction lease : leaseTransactions)
            if (TransactionService.isOverlapping(now, now, lease.getStartDate(), lease.getEndDate()))
                return false;
        return true;
    }

}
