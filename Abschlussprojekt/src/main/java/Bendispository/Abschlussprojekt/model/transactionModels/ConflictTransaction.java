package Bendispository.Abschlussprojekt.model.transactionModels;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class ConflictTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private boolean leaserGotTheDepositBack;

    private boolean lenderAccepted;

    private boolean leaserAccepted;

    private int validationTime;

    private String commentary;

    @OneToOne(cascade = CascadeType.PERSIST)
    LeaseTransaction leaseTransaction;

}