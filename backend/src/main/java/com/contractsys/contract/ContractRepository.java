package com.contractsys.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.deleted=false " +
           "AND (c.name LIKE %:keyword% OR c.contractNo LIKE %:keyword% OR c.customer.name LIKE %:keyword%) " +
           "AND (:status IS NULL OR c.status = :status) ORDER BY c.createdAt DESC")
    Page<Contract> search(@Param("keyword") String keyword, @Param("status") ContractStatus status, Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.deleted=false " +
           "AND (c.drafter.id = :userId OR c.id IN " +
           "(SELECT t.contract.id FROM ContractTask t WHERE t.assignee.id = :userId)) " +
           "AND (c.name LIKE %:keyword% OR c.contractNo LIKE %:keyword% OR c.customer.name LIKE %:keyword%) " +
           "AND (:status IS NULL OR c.status = :status) ORDER BY c.createdAt DESC")
    Page<Contract> searchByRelatedUser(@Param("keyword") String keyword, @Param("status") ContractStatus status,
                                       @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT h FROM ContractStateHistory h WHERE h.contract.deleted=false " +
           "ORDER BY h.createdAt DESC")
    Page<ContractStateHistory> findHistory(Pageable pageable);

    @Query("SELECT h FROM ContractStateHistory h WHERE h.contract.deleted=false " +
           "AND (h.contract.name LIKE %:keyword% OR h.contract.contractNo LIKE %:keyword% OR h.operator.username LIKE %:keyword%) " +
           "ORDER BY h.createdAt DESC")
    Page<ContractStateHistory> findHistoryByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Contract> findByStatusAndDeletedFalse(ContractStatus status);

    long countByDeletedFalse();
    long countByDeletedFalseAndStatus(ContractStatus status);
}

