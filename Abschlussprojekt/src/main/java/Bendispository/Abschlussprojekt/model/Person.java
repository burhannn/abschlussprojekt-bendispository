package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Pattern(regexp="[a-zA-ZöäüÖÄÜß]+")
    private String lastName;

	@Pattern(regexp="[a-zA-ZöäüÖÄÜß]+")
    private String firstName;

	@Pattern(regexp="[a-zA-Z0-9öäüÖÄÜß]+")
    @Column(nullable = false, unique = true)
    private String username;


	@Pattern(regexp=".{8,}")
    private String password;

    @Email
    private String email;

	@Digits(integer = 9, fraction = 0)
    private int bankaccount;

    @Pattern(regexp="[a-zA-ZöäüÖÄÜß 0-9]+")
    private String city;


    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    private List<Item> Items;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<LeaseTransaction> leaseTransactions;

    public void addLeaseTransaction(LeaseTransaction leaseTransaction){
        leaseTransactions.add(leaseTransaction);
    }

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Rating> ratings;

    public void addRating(Rating rating){
        ratings.add(rating);
    }
    public int getAverageRatings() {
        if(ratings.size() == 0){
            return -1;
        }
        return ratings.stream().mapToInt(Rating::getRatingPoints).sum()/ratings.size();
    }
}
