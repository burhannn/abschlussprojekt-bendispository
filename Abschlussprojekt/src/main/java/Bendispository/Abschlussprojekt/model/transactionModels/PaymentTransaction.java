package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Person;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PaymentTransaction {

    // relies on ProPay
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    private Person leaser;

    @ManyToOne
    private Person lender;

    private int amount;

    private boolean paymentIsConcluded;

    // DEPOSIT, DAMAGES, RENTPRICE
    // DEPOSIT => was blocked
    private PaymentType type;

    @ManyToOne
    LeaseTransaction leaseTransaction;

    public PaymentTransaction(Person leaser, Person lender, int amount){
        this.leaser = leaser;
        this.lender = lender;
        this.amount = amount;
    }

}
