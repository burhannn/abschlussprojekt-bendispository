package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RequestController {
    @Autowired
    RequestRepo requestRepo;

}
