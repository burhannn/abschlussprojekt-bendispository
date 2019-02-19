package Bendispository.Abschlussprojekt.model;

import lombok.Data;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class Rating {
    private Request request;
    private Person rater;
    private Integer ratingPoints;

    public Rating(){}

    public Rating(Request request, Person rater, Integer ratingPoints){
        this.request = request;
        this.rater = rater;
        this.ratingPoints = ratingPoints;
    }
}
