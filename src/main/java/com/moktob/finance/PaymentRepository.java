package com.moktob.finance;

import com.moktob.common.PayerType;
import com.moktob.common.PaymentMethod;
import com.moktob.common.PaymentType;
import com.moktob.common.TenantContextHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByClientId(Long clientId);
    
    Optional<Payment> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.payerType = :payerType")
    List<Payment> findByClientIdAndPayerType(@Param("clientId") Long clientId, @Param("payerType") PayerType payerType);
    
    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.paymentType = :paymentType")
    List<Payment> findByClientIdAndPaymentType(@Param("clientId") Long clientId, @Param("paymentType") PaymentType paymentType);
    
    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.paymentMethod = :paymentMethod")
    List<Payment> findByClientIdAndPaymentMethod(@Param("clientId") Long clientId, @Param("paymentMethod") PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.paymentDate = :date")
    List<Payment> findByClientIdAndPaymentDate(@Param("clientId") Long clientId, @Param("date") LocalDate date);
    
    @Query("SELECT p FROM Payment p WHERE p.clientId = :clientId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByClientIdAndPaymentDateBetween(@Param("clientId") Long clientId, 
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.clientId = :clientId")
    BigDecimal getTotalPaymentsByClientId(@Param("clientId") Long clientId);
}
