package com.jobportal.repository;

import com.jobportal.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    Optional<Resume> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    long countByOwnerId(Long ownerId);

    Optional<Resume> findByOwnerIdAndPrimaryTrue(Long ownerId);

    @Modifying
    @Query("UPDATE Resume r SET r.primary = false WHERE r.owner.id = :ownerId")
    void clearPrimaryForOwner(@Param("ownerId") Long ownerId);
}
