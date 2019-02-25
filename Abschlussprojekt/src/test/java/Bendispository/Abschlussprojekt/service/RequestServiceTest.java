package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.hibernate.jdbc.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestServiceTest {

    @Mock
    private Clock clock;

    @Mock
    RedirectAttributes redirectAttributes;

    private Clock fakeClock;

    @MockBean
    RequestRepo requestRepo;

    @Spy
    @InjectMocks
    RequestService requestService;

    Request r1;
    Request r2;
    Request r3;
    Request r4;

    @Before
    public void sup(){
        fakeClock = Clock.fixed(Instant.parse("2019-01-03T10:15:30.00Z"), ZoneId.of("UTC"));
        doReturn(fakeClock.instant()).when(clock).instant();
        doReturn(fakeClock.getZone()).when(clock).getZone();

        r1 = new Request();
        r1.setStartDate(LocalDate.of(2019,1,1));
        r1.setEndDate(LocalDate.of(2019,1,2));
        r2 = new Request();
        r2.setStartDate(LocalDate.of(2019,1,2));
        r2.setEndDate(LocalDate.of(2019,1,3));
        r3 = new Request();
        r3.setStartDate(LocalDate.of(2019,1,3));
        r3.setEndDate(LocalDate.of(2019,1,4));
        r4 = new Request();
        r4.setStartDate(LocalDate.of(2019,1,4));
        r4.setEndDate(LocalDate.of(2019,1,5));
        requestRepo.saveAll(Arrays.asList(r1,r2,r3,r4));
        when(requestRepo.findAll()).thenReturn(Arrays.asList(r1,r2,r3,r4));
        //when(redirectAttributes.addFlashAttribute(anyString(),anyString())).thenReturn(new Re)
        //when(requestRepo.delete()).then
    }

    /*@Test
    public void deletingTwoObsoleteRequests(){
        //RequestService requestService = mock(RequestService.class);

        List<Request> repoList = requestRepo.findAll();
        Mockito.doCallRealMethod().when(requestService).deleteObsoleteRequests(repoList);
        //when(requestService.deleteObsoleteRequests(anyList())).thenCallRealMethod();
        List<Request> expectedlyRemoved = new ArrayList<Request>(){{add(r1);add(r2);}};
        List<Request> toRemove = requestService.deleteObsoleteRequests(repoList);

        assertEquals(expectedlyRemoved, toRemove);

    }*/

    @Test
    public void checkingRequestedDate(){
        //RequestService requestService = mock(RequestService.class);
        when(requestService.checkRequestedDate(anyString(),anyString())).thenCallRealMethod();

        boolean check = requestService.checkRequestedDate("2019.01.02", "2019.01.03");
        assertEquals(false, check);
        check = requestService.checkRequestedDate("2019-01-02", "2019-01-02");
        assertEquals(false, check);
        check = requestService.checkRequestedDate("2019-01-03", "2019-01-02");
        assertEquals(false, check);
        check = requestService.checkRequestedDate("2019-01-02", "2019-01-03");      // false due to LocalDate.now(fakeClock)!
        assertEquals(false, check);
        check = requestService.checkRequestedDate("2019-01-05", "2019-01-06");
        assertEquals(true, check);
    }

}