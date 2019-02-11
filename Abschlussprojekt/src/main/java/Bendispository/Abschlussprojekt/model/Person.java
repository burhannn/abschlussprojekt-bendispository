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

    private String username;

    private String email;

    private String konto;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Item> lentItemFrom;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Item> lentItemTo;
}
