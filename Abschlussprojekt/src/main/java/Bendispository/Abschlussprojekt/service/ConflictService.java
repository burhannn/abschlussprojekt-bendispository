package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConflictService {

    private final ConflictTransactionRepo conflictTransactionRepo;

    private final TransactionService transactionService;

    private RatingRepo ratingRepo;

    @Autowired
    public ConflictService(LeaseTransactionRepo leaseTransactionRepo,
                           RequestRepo requestRepo,
                           ProPaySubscriber proPaySubscriber,
                           PaymentTransactionRepo paymentTransactionRepo,
                           ConflictTransactionRepo conflictTransactionRepo,
                           RatingRepo ratingRepo) {
        super();
        this.conflictTransactionRepo = conflictTransactionRepo;
        this.transactionService = new TransactionService(leaseTransactionRepo, requestRepo, proPaySubscriber, paymentTransactionRepo, conflictTransactionRepo, ratingRepo);
    }

    public boolean resolveConflict(ConflictTransaction conflict, ConflictTransactionRepo conflictTransactionRepo, boolean depositBackToLeaser){
        conflict.setLeaserAccepted(true);
        conflict.setLenderAccepted(true);
        conflict.setLeaserGotTheDepositBack(depositBackToLeaser);
        conflictTransactionRepo.save(conflict);
        if(depositBackToLeaser)
            return transactionService.itemIsIntact(conflict.getLeaseTransaction());
        else
            return transactionService.itemIsNotIntactConclusion(conflict.getLeaseTransaction());
    }
}
