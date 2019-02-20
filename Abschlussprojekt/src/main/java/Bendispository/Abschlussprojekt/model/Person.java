package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    private String email;

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
