package Bendispository.Abschlussprojekt.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL,
               fetch = FetchType.LAZY)
    private Person requester;

    private Item requestedItem;

    private int duration;

    // value = "denied", "accepted", "pending"
    private RequestStatus status = RequestStatus.PENDING;

}
