package com.jobportal.repository;

import com.jobportal.entity.User;
import com.jobportal.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findAllByRoleName(@Param("roleName") RoleName roleName, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") RoleName roleName);

    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<User> searchUsers(@Param("q") String q, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = :enabled")
    long countByEnabled(@Param("enabled") boolean enabled);
}
