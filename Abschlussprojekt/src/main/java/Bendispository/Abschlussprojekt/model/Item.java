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

    private boolean leaseOrSell;

    private double retailPrice;

    private int deposit;

    @Embedded
    private UploadFile uploadFile;

    private int costPerDay;

    private String place;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Person owner;
}
