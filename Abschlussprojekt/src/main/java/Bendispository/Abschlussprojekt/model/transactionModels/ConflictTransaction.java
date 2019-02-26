package Bendispository.Abschlussprojekt.model.transactionModels;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString(exclude = {"leaseTransaction"})
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