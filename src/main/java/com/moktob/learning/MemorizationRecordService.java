package com.moktob.learning;

import com.moktob.common.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemorizationRecordService {
    
    private final MemorizationRecordRepository memorizationRecordRepository;
    
    public List<MemorizationRecord> getAllRecords() {
        Long clientId = TenantContextHolder.getTenantId();
        return memorizationRecordRepository.findByClientId(clientId);
    }
    
    public Optional<MemorizationRecord> getRecordById(Long id) {
        Long clientId = TenantContextHolder.getTenantId();
        return memorizationRecordRepository.findByClientIdAndId(clientId, id);
    }
    
    public MemorizationRecord saveRecord(MemorizationRecord record) {
        Long clientId = TenantContextHolder.getTenantId();
        record.setClientId(clientId);
        return memorizationRecordRepository.save(record);
    }
    
    public void deleteRecord(Long id) {
        memorizationRecordRepository.deleteById(id);
    }
    
    public List<MemorizationRecord> getRecordsByStudent(Long studentId) {
        Long clientId = TenantContextHolder.getTenantId();
        return memorizationRecordRepository.findByClientIdAndStudentId(clientId, studentId);
    }
    
    public List<MemorizationRecord> getRecordsBySurah(String surahName) {
        Long clientId = TenantContextHolder.getTenantId();
        return memorizationRecordRepository.findByClientIdAndSurahName(clientId, surahName);
    }
}
