package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String lastName;

    private String firstName;

    @Column(nullable = false, unique = true)
    private String username;

    private String email;

    private int account;

    private String city;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Item> Items;

}