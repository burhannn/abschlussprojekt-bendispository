package Bendispository.Abschlussprojekt.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@ToString(exclude = "owner")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    private boolean available;

    private int deposit;

    private int costPerDay;

    @ManyToOne(cascade = CascadeType.PERSIST,
               fetch = FetchType.EAGER)
    private Person owner;
}
