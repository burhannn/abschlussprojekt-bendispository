package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.MarketType;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(SpringRunner.class)
public class ItemServiceTest {

	@MockBean
	ItemRepo itemRepo;

	@MockBean
	PersonsRepo personsRepo;

	@MockBean
	AuthenticationService authenticationService;

	Item itemSell;
	Item itemBuy;
	Person person1;
	private ItemService itemService;
	private Clock clock;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		person1 = new Person();
		itemSell = new Item();
		itemBuy = new Item();

		person1.setFirstName("mandy");
		person1.setLastName("moraru");
		person1.setCity("kölle");
		person1.setEmail("momo@gmail.com");
		person1.setUsername("momo");
		person1.setPassword("abcdabcd");
		person1.setId(1L);

		itemSell.setName("stuhl");
		itemSell.setDeposit(40);
		itemSell.setDescription("bin billig");
		itemSell.setCostPerDay(10);
		itemSell.setId(2L);
		itemSell.setOwner(person1);
		itemSell.setMarketType(MarketType.LEND);

		itemBuy.setName("stuhl");
		itemBuy.setDeposit(40);
		itemBuy.setDescription("bin billig");
		itemBuy.setCostPerDay(10);
		itemBuy.setId(3L);
		itemBuy.setOwner(person1);
		itemBuy.setMarketType(MarketType.LEND);

		List<Item> items1 = new ArrayList<Item>();

		items1.addAll(Arrays.asList(itemSell, itemBuy));
		person1.setItems(items1);


		itemRepo.saveAll(Arrays.asList(itemSell, itemBuy));
		personsRepo.saveAll(Arrays.asList(person1));

		when(personsRepo.findById(1L))
				.thenReturn(Optional.ofNullable(person1));

		when(itemRepo.findById(2L))
				.thenReturn(Optional.ofNullable(itemSell));

		doReturn(Optional.of(itemBuy)).when(itemRepo).findById(anyLong());

		when(authenticationService.getCurrentUser()).thenReturn(person1);
		itemService = new ItemService(itemRepo, personsRepo, authenticationService, clock);
	}

	@After
	public void delete() {
		personsRepo.deleteAll();
		itemRepo.deleteAll();
	}

	@Test
	@WithMockUser(username = "momo", password = "abcdabcd")
	public void checkAddItem() {
		itemService.addItem(itemSell, MarketType.SELL);
		Mockito.verify(itemRepo, times(1)).save(itemSell);
	}

	@Test
	@WithMockUser(username = "momo", password = "abcdabcd")
	public void checkDeleteItem() {

		itemService.deleteItem(3L);
		Mockito.verify(itemRepo, times(1)).deleteById(anyLong());
	}

	@Test
	@WithMockUser(username = "momo", password = "abcdabcd")
	public void checkEditItem() {

		Item inputItem = new Item();
		inputItem.setRetailPrice(60);

		itemService.editItem(inputItem, Optional.ofNullable(itemSell), 3L);
		Mockito.verify(itemRepo, times(1)).save(inputItem);
	}

}