package com.codecode.payment.controller;

import com.codecode.payment.dto.PaymentContactInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentContactInfoDTO paymentContactInfoDTO;

    @Value("${build.version}")
    public String buildVersion;

    public PaymentController(PaymentContactInfoDTO paymentContactInfoDTO) {
        this.paymentContactInfoDTO = paymentContactInfoDTO;
    }

    @GetMapping("/build-version")
    public ResponseEntity<String> getBuildVersion() {
        return new ResponseEntity<>(buildVersion, HttpStatus.OK);
    }

    @GetMapping("/contact-info")
    public ResponseEntity<PaymentContactInfoDTO> getContactInfo() {
        return new ResponseEntity<>(paymentContactInfoDTO, HttpStatus.OK);
    }
}
