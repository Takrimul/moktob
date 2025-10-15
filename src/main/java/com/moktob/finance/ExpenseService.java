package com.moktob.finance;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public List<Expense> getAllExpenses() {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.findByClientId(clientId);
    }
    
    public Optional<Expense> getExpenseById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.findByClientIdAndId(clientId, id);
    }
    
    public Expense saveExpense(Expense expense) {
        Long clientId = TenantContextHolder.getTenantId();
        expense.setClientId(clientId);
        return expenseRepository.save(expense);
    }
    
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
    
    public List<Expense> getExpensesByCategory(String category) {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.findByClientIdAndCategory(clientId, category);
    }
    
    public List<Expense> getExpensesByDate(LocalDate date) {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.findByClientIdAndExpenseDate(clientId, date);
    }
    
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.findByClientIdAndExpenseDateBetween(clientId, startDate, endDate);
    }
    
    public BigDecimal getTotalExpenses() {
        Long clientId = TenantContextHolder.getTenantId();
        return expenseRepository.getTotalExpensesByClientId(clientId);
    }
}
