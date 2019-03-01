package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@ToString(exclude = {"requestedItem", "requester", "leaseTransaction"})
@Entity
public class Request {

	// Tage, kann auf stunden gewechselt werden
	private static final int VALIDATION = 2;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne(cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	private Person requester;

	@ManyToOne(cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	private Item requestedItem;

	private String itemName;

	private String ownerName;

	private int duration;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate;

	//Zeit, die der Lender hat, um den Request zu bearbeiten
	private int validationTime = VALIDATION;

	// value = "denied", "approved", "pending"
	private RequestStatus status = RequestStatus.PENDING;

	@OneToOne(cascade = CascadeType.PERSIST,
			fetch = FetchType.EAGER)
	private LeaseTransaction leaseTransaction;
}
