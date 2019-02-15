package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@Entity
public class ConflictTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private boolean lenderAccepted;

    private boolean leaserAccepted;

    private int validationTime;

    private String commentary;

    @OneToOne
    LeaseTransaction leaseTransaction;

}