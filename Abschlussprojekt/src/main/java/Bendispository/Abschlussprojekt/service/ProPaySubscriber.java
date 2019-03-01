package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.model.transactionModels.Reservation;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Component
public class ProPaySubscriber {

	final PersonsRepo personsRepo;

	final LeaseTransactionRepo leaseTransactionRepo;

	private static final String SCHEME = "http";
	private static final String HOST = "propay:8888";


	@Autowired
	public ProPaySubscriber(PersonsRepo personsRepo, LeaseTransactionRepo leaseTransactionRepo) {
		super();
		this.personsRepo = personsRepo;
		this.leaseTransactionRepo = leaseTransactionRepo;
	}

	public int makeDeposit(Request request) {
		Reservation reservation =
				makeReservation(
						request.getRequester().getUsername(),
						request.getRequestedItem().getOwner().getUsername(),
						(double) request.getRequestedItem().getDeposit());
		if (reservation == null) return -1;
		return reservation.getId();
	}

	protected Reservation makeReservation(String leaserName, String lenderName, double deposit) {
		try {
			final Mono<Reservation> mono = WebClient
					.create()
					.post()
					.uri(builder ->
							builder.scheme(SCHEME)
									.host(HOST)
									.pathSegment("reservation", "reserve", leaserName, lenderName)
									.queryParam("amount", deposit)
									.build())
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(Reservation.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			return mono.block();
		} catch (Exception e) {
			return null;
		}
	}

	public ProPayAccount releaseReservation(String username, int id) {
		try {
			final Mono<ProPayAccount> mono = WebClient
					.create()
					.post()
					.uri(builder ->
							builder.scheme(SCHEME)
									.host(HOST)
									.pathSegment("reservation", "release", username)
									.queryParam("reservationId", id)
									.build())
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(ProPayAccount.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			return mono.block();
		} catch (Exception e) {
			return null;
		}
	}

	public ProPayAccount releaseReservationAndPunishUser(String username, int id) {
		try {
			final Mono<ProPayAccount> mono = WebClient
					.create()
					.post()
					.uri(builder ->
							builder.scheme(SCHEME)
									.host(HOST)
									.pathSegment("reservation", "punish", username)
									.queryParam("reservationId", id)
									.build())
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(ProPayAccount.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			return mono.block();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean checkDeposit(double requiredDeposit, String username) {
		ProPayAccount account = getAccount(username);
		if (account == null || account.getAmount() < requiredDeposit)
			return false;
		return true;
	}

	public ProPayAccount getAccount(String username) {
		try {
			final Mono<ProPayAccount> mono = WebClient
					.create()
					.get()
					.uri(builder ->
							builder.scheme(SCHEME)
									.host(HOST)
									.pathSegment("account", username)
									.build())
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(ProPayAccount.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			return mono.block();
		} catch (Exception e) {
			return null;
		}
	}

	public ProPayAccount chargeAccount(String username, double value) {
		try {
			final Mono<ProPayAccount> mono = WebClient
					.create()
					.post()
					.uri(builder ->
							builder.scheme(SCHEME)
									.host(HOST)
									.pathSegment("account", username)
									.queryParam("amount", value)
									.build())
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(ProPayAccount.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			return mono.block();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean transferMoney(String leaserName, String lenderName, double amount) {
		return executeTransfer(leaserName, lenderName, amount);
	}

	protected boolean executeTransfer(String leaserName, String lenderName, double value) {
		URI uri = UriComponentsBuilder
				.newInstance()
				.scheme(SCHEME)
				.host(HOST)
				.pathSegment("account", leaserName, "transfer", lenderName)
				.queryParam("amount", value)
				.build()
				.toUri();
		try {
			final Mono<HttpHeaders> mono = WebClient
					.create()
					.post()
					.uri(uri)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.retrieve()
					.bodyToMono(HttpHeaders.class)
					.timeout(Duration.ofSeconds(2L))
					.retry(4L);
			mono.block();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
