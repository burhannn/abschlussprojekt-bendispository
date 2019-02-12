package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.repo.PaymentTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PaymentController {

    @Autowired
    PaymentTransactionRepo paymentTransactionRepo;



}
