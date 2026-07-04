package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /*
     * ManyToMany with EAGER fetch so Spring Security can load authorities
     * without a session — safe because the role set is very small (1-2 entries).
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "postedBy", cascade = CascadeType.REMOVE, orphanRemoval = false)
    @Builder.Default
    private List<Job> postedJobs = new ArrayList<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.REMOVE, orphanRemoval = false)
    @Builder.Default
    private List<JobApplication> applications = new ArrayList<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resume> resumes = new ArrayList<>();

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
