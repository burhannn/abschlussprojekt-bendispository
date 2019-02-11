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

    private String beschreibung;

    private boolean verfuegbar;

    private int kaution;

    private int CostsPerDay;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Person owner;
}
