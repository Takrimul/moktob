package com.moktob.finance;

import com.moktob.common.BaseEntity;
import com.moktob.common.PayerType;
import com.moktob.common.PaymentMethod;
import com.moktob.common.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payer_type")
    private PayerType payerType;
    
    @Column(name = "payer_id")
    private Long payerId;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "reference_no", length = 100)
    private String referenceNo;
    
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
}
