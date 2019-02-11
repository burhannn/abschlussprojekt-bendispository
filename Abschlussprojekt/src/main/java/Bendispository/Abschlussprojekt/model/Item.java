package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

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

    private int CostPerDay;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Person owner;
}
