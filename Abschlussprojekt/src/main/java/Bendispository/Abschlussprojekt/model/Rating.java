package Bendispository.Abschlussprojekt.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString(exclude = "rater")
@Data
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Request request;

	@ManyToOne
	private Person rater;
	private Integer ratingPoints;

	public Rating() {
	}

	public Rating(Request request, Person rater, Integer ratingPoints) {
		this.request = request;
		this.rater = rater;
		this.ratingPoints = ratingPoints;
	}
}
