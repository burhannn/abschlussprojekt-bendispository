package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private Request request;
    @ManyToOne
    private Person rater;
    private Integer ratingPoints;

    public Rating(){}

    public Rating(Request request, Person rater, Integer ratingPoints){
        this.request = request;
        this.rater = rater;
        this.ratingPoints = ratingPoints;
    }
}
