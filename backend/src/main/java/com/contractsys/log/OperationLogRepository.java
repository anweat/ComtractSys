package com.contractsys.log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    @Query("SELECT o FROM OperationLog o ORDER BY o.createdAt DESC")
    Page<OperationLog> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT o FROM OperationLog o WHERE o.operator.username LIKE %:keyword% " +
           "OR o.operation LIKE %:keyword% OR o.detail LIKE %:keyword% ORDER BY o.createdAt DESC")
    Page<OperationLog> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
