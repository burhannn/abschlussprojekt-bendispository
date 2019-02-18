package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.model.transactionModels.Reservation;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class ProPaySubscriber {

    final PersonsRepo personsRepo;

    final LeaseTransactionRepo leaseTransactionRepo;

    @Autowired
    public ProPaySubscriber(PersonsRepo personsRepo, LeaseTransactionRepo leaseTransactionRepo) {
        super();
        this.personsRepo = personsRepo;
        this.leaseTransactionRepo = leaseTransactionRepo;
    }


    public int makeDeposit(Request request){
        Reservation reservation = makeReservation(request.getRequester().getUsername(),
                                                  request.getRequestedItem().getOwner().getUsername(),
                                                  request.getRequestedItem().getDeposit(),
                                                  Reservation.class);
        return reservation.getId();
    }

    private <T> T makeReservation(String leaserName, String lenderName, int deposit, Class<T> type) {
        final Mono<T> mono = WebClient
                .create()
                .get()
                .uri(builder ->
                        builder.scheme("https")
                                .host("propra-propay.herokuapp.com")
                                .pathSegment("reservation", "reserve", lenderName, leaserName)
                                .queryParam("amount", deposit)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(type);
        return mono.block();
    }

    private <T> T releaseReservation(String username, int id, Class<T> type) {
        final Mono<T> mono = WebClient
                .create()
                .get()
                .uri(builder ->
                        builder.scheme("https")
                                .host("propra-propay.herokuapp.com")
                                .pathSegment("reservation", "release", username)
                                .queryParam("reservationId", id)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(type);
        return mono.block();
    }

    private <T> T releaseReservationAndPunishUser(String username, int id, Class<T> type) {
        final Mono<T> mono = WebClient
                .create()
                .get()
                .uri(builder ->
                        builder.scheme("https")
                                .host("propra-propay.herokuapp.com")
                                .pathSegment("reservation", "punish", username)
                                .queryParam("reservationId", id)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(type);
        return mono.block();
    }

    public boolean checkDeposit(int requiredDeposit, String username){
        ProPayAccount account = getAccount(username, ProPayAccount.class);
        if(account.getAmount() >= requiredDeposit)
            return true;
        return false;
    }

    private <T> T getAccount(String username, Class<T> type) {
        final Mono<T> mono = WebClient
                        .create()
                        .get()
                        .uri(builder ->
                                 builder.scheme("https")
                                        .host("propra-propay.herokuapp.com")
                                        .pathSegment("account", username)
                                        .build())
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToMono(type);
        return mono.block();
    }

    public String transferMoney(String leaserName, String lenderName, int amount){
        executeTransfer(leaserName, lenderName, amount);
        return "";
    }

    private void executeTransfer(String leaserName, String lenderName, int value) {
        URI uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("propra-propay.herokuapp.com")
                .pathSegment("account", leaserName, "transfer", lenderName)
                .queryParam("amount", value)
                .build()
                .toUri();

        // Wie response code checken?????
        // abhängig davon weitermachen...
    }

    public void chargeAccount(String username, int value){
        final Mono<ProPayAccount> mono = WebClient
                .create()
                .get()
                .uri(builder ->
                        builder.scheme("https")
                                .host("propra-propay.herokuapp.com")
                                .pathSegment("account", username)
                                .queryParam("amount", value)
                                .build())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .retrieve()
                .bodyToMono(ProPayAccount.class);
        mono.block();
    }
}
