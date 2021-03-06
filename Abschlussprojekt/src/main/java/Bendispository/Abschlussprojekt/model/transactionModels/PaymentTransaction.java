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
	@ManyToOne(cascade = CascadeType.ALL,
			fetch = FetchType.EAGER)
	LeaseTransaction leaseTransaction;
	@ManyToOne
	private Person leaser;
	@ManyToOne
	private Person lender;
	private double amount;
	private boolean paymentIsConcluded;
	// DEPOSIT, DAMAGES, RENTPRICE
	// DEPOSIT => was blocked
	private PaymentType type;

	public PaymentTransaction(Person leaser, Person lender, double amount) {
		this.leaser = leaser;
		this.lender = lender;
		this.amount = amount;
	}

	public PaymentTransaction() {
	}

	public String toString() {
		return "amount:" + amount + " paymentisconcluded:" + paymentIsConcluded + " type:" + type;
	}

}
