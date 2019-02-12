package Bendispository.Abschlussprojekt.Model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private boolean free;

    private int deposit;

    private int costPerDay;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Person owner;
}
