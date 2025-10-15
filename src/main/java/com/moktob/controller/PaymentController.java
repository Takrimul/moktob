package com.moktob.controller;

import com.moktob.finance.Payment;
import com.moktob.finance.PaymentService;
import com.moktob.common.PayerType;
import com.moktob.common.PaymentMethod;
import com.moktob.common.PaymentType;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.savePayment(payment));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        payment.setId(id);
        return ResponseEntity.ok(paymentService.savePayment(payment));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/payer-type/{payerType}")
    public ResponseEntity<List<Payment>> getPaymentsByPayerType(@PathVariable PayerType payerType) {
        return ResponseEntity.ok(paymentService.getPaymentsByPayerType(payerType));
    }
    
    @GetMapping("/payment-type/{paymentType}")
    public ResponseEntity<List<Payment>> getPaymentsByPaymentType(@PathVariable PaymentType paymentType) {
        return ResponseEntity.ok(paymentService.getPaymentsByPaymentType(paymentType));
    }
    
    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<List<Payment>> getPaymentsByPaymentMethod(@PathVariable PaymentMethod paymentMethod) {
        return ResponseEntity.ok(paymentService.getPaymentsByPaymentMethod(paymentMethod));
    }
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Payment>> getPaymentsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(paymentService.getPaymentsByDate(date));
    }
    
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalPayments() {
        return ResponseEntity.ok(paymentService.getTotalPayments());
    }
}
