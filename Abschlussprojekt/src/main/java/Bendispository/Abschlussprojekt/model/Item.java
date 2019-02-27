package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.MarketType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Entity
@ToString(exclude = "owner")
@Data
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Pattern(regexp = "[a-zA-ZöäüÖÄÜß 0-9]+")
	private String name;

	@Pattern(regexp = "[a-zA-ZöäüÖÄÜß .,?!0-9]+")
	private String description;

	private MarketType marketType;

	private boolean active = true;

	@Min(0)
	@Max(10000000)
	private int retailPrice;

	@Min(0)
	@Max(10000000)
	private int deposit;

	@Embedded
	private UploadFile uploadFile;

	@Min(0)
	@Max(10000000)
	private int costPerDay;

	@Pattern(regexp = "[a-zA-ZöäüÖÄÜß 0-9]+")
	private String place;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Person owner;
}