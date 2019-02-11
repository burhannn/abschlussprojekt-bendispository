package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class LeaseTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @@OneToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private Person leaser;

    @OneToOne(cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    private Person lender;

    @OneToOne(cascade = CascadeType.ALL,
              fetch = FetchType.LAZY)
    private Item item;

}
