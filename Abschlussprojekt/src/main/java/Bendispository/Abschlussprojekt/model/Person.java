package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;
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

}
