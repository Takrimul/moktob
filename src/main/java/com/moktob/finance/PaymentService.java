package com.moktob.finance;

import com.moktob.common.PayerType;
import com.moktob.common.PaymentMethod;
import com.moktob.common.PaymentType;
import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientId(clientId);
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndId(clientId, id);
    }
    
    public Payment savePayment(Payment payment) {
        Long clientId = TenantContextHolder.getTenantId();
        payment.setClientId(clientId);
        return paymentRepository.save(payment);
    }
    
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
    
    public List<Payment> getPaymentsByPayerType(PayerType payerType) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndPayerType(clientId, payerType);
    }
    
    public List<Payment> getPaymentsByPaymentType(PaymentType paymentType) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndPaymentType(clientId, paymentType);
    }
    
    public List<Payment> getPaymentsByPaymentMethod(PaymentMethod paymentMethod) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndPaymentMethod(clientId, paymentMethod);
    }
    
    public List<Payment> getPaymentsByDate(LocalDate date) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndPaymentDate(clientId, date);
    }
    
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.findByClientIdAndPaymentDateBetween(clientId, startDate, endDate);
    }
    
    public BigDecimal getTotalPayments() {
        Long clientId = TenantContextHolder.getTenantId();
        return paymentRepository.getTotalPaymentsByClientId(clientId);
    }
}
