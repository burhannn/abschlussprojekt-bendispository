package Bendispository.Abschlussprojekt.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@ToString(exclude = "Items")
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

    private int bankaccount;

    private String city;

    @OneToMany(cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    private List<Item> Items;

}
