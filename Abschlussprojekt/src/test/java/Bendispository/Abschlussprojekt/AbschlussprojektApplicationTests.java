package Bendispository.Abschlussprojekt;

import Bendispository.Abschlussprojekt.repo.ItemRepo;
import Bendispository.Abschlussprojekt.repo.PersonsRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebMvcTest
public class AbschlussprojektApplicationTests {

	@Autowired
	MockMvc mvc;

	@MockBean
	ItemRepo itemRepo;

	@MockBean
	PersonsRepo personsRepo;

	@Test
	public void retrieve() throws Exception {
		mvc.perform(get("/")).andExpect(status().isOk());
		//mvc.perform(get("/profile")).andExpect(status().isOk());
		//mvc.perform(get("/Item")).andExpect(status().isOk());
		mvc.perform(get("/profilub")).andExpect(status().isOk());
		mvc.perform(get("/addItem")).andExpect(status().isOk());
		mvc.perform(get("/registration")).andExpect(status().isOk());
	}

	@Test
	public void addItem() throws Exception {

	}

	@Test
	public void ItemProfile() throws Exception{

	}

	@Test
	public void Overview() throws Exception {

	}

	@Test
	public void UserProfile() throws Exception {

	}

	@Test
	public void profileDetails() throws Exception {

	}

	@Test
	public void checkRegistration() throws Exception {

	}
}

