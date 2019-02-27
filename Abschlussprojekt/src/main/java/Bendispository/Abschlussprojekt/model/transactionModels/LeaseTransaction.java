package Bendispository.Abschlussprojekt.model.transactionModels;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString(exclude = {"item", "payments", "conflictTransaction"})
@Entity
public class LeaseTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private Person leaser;

	@ManyToOne(fetch = FetchType.EAGER)
	private Item item;

	private Long requestId;

	private int depositId;

	private boolean itemIsReturned = false;

	private boolean itemIsIntact = true;

	private boolean leaseIsConcluded = false;

	private int lengthOfTimeframeViolation = 0;

	private boolean timeframeViolation = false;

	// number of days
	private int duration;
	private LocalDate startDate;
	private LocalDate endDate;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL)
	private List<PaymentTransaction> payments = new ArrayList<PaymentTransaction>();

	@OneToOne(cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	private ConflictTransaction conflictTransaction;

	public void addLeaseTransaction(Request request, int depositId) {
		this.setItem(request.getRequestedItem());
		this.setLeaser(request.getRequester());
		this.setRequestId(request.getId());
		this.setDuration(request.getDuration());
		this.startDate = request.getStartDate();
		this.endDate = request.getEndDate();
		this.depositId = depositId;
		request.getRequester().addLeaseTransaction(this);
	}

	public void addPaymentTransaction(PaymentTransaction paymentTransaction) {
		this.payments.add(paymentTransaction);
	}
}
