package Bendispository.Abschlussprojekt.Service;

import lombok.Data;

@Data
public class ProPayAccount {

    private String account;
    private int amount;
    private Reservation[] reservations;

}
