package com.moktob.finance;

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
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByClientId(Long clientId);
    
    Optional<Expense> findByClientIdAndId(Long clientId, Long id);
    
    @Query("SELECT e FROM Expense e WHERE e.clientId = :clientId AND e.category = :category")
    List<Expense> findByClientIdAndCategory(@Param("clientId") Long clientId, @Param("category") String category);
    
    @Query("SELECT e FROM Expense e WHERE e.clientId = :clientId AND e.expenseDate = :date")
    List<Expense> findByClientIdAndExpenseDate(@Param("clientId") Long clientId, @Param("date") LocalDate date);
    
    @Query("SELECT e FROM Expense e WHERE e.clientId = :clientId AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<Expense> findByClientIdAndExpenseDateBetween(@Param("clientId") Long clientId, 
                                                    @Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.clientId = :clientId")
    BigDecimal getTotalExpensesByClientId(@Param("clientId") Long clientId);
}
