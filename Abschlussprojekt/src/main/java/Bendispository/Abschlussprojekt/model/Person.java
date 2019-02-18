package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import lombok.Data;

import javax.persistence.*;
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
    private List<Item> items;

    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private List<LeaseTransaction> leaseTransactions;

    public void addLeaseTransaction(LeaseTransaction leaseTransaction){
        leaseTransactions.add(leaseTransaction);
    }
}
