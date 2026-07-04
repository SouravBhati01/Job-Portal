package com.jobportal.config;

import com.jobportal.entity.Job;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import com.jobportal.enums.RoleName;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RoleRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobRepository jobRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Ensure all three roles exist in the roles table
            Role adminRole = ensureRole(RoleName.ROLE_ADMIN);
            Role recruiterRole = ensureRole(RoleName.ROLE_RECRUITER);
            Role applicantRole = ensureRole(RoleName.ROLE_APPLICANT);
            log.info("Roles initialised: ROLE_ADMIN, ROLE_RECRUITER, ROLE_APPLICANT");

            // Seed the admin account
            if (!userRepository.existsByEmail("admin@jobportal.com")) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("Portal")
                        .email("admin@jobportal.com")
                        .password(passwordEncoder.encode("Admin@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(adminRole)))
                        .build();
                userRepository.save(admin);
                log.info("Admin account created  →  admin@jobportal.com  /  Admin@1234");
            }

            // Seed a demo recruiter
            if (!userRepository.existsByEmail("recruiter@jobportal.com")) {
                User recruiter = User.builder()
                        .firstName("Demo")
                        .lastName("Recruiter")
                        .email("recruiter@jobportal.com")
                        .password(passwordEncoder.encode("Recruit@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(recruiterRole)))
                        .build();
                userRepository.save(recruiter);
                log.info("Demo recruiter created  →  recruiter@jobportal.com  /  Recruit@1234");
            }

            // Seed a demo applicant
            if (!userRepository.existsByEmail("applicant@jobportal.com")) {
                User applicant = User.builder()
                        .firstName("Demo")
                        .lastName("Applicant")
                        .email("applicant@jobportal.com")
                        .password(passwordEncoder.encode("Apply@1234"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(applicantRole)))
                        .build();
                userRepository.save(applicant);
                log.info("Demo applicant created  →  applicant@jobportal.com  /  Apply@1234");
            }

            // Seed default Tech/CSE Jobs
            if (jobRepository.count() == 0) {
                User recruiter = userRepository.findByEmail("recruiter@jobportal.com").orElse(null);
                if (recruiter != null) {
                    Job job1 = Job.builder()
                            .title("Software Engineer (Backend)")
                            .company("Google")
                            .description("Join our engineering team to design, build, and maintain high-throughput backend services and APIs using Spring Boot, Hibernate, and PostgreSQL. You will be responsible for scaling database schemas and optimizing application performance.")
                            .requirements("Strong proficiency in Java 17+ and Spring Boot microservices.\nExperience with database design, JPA/Hibernate, and query optimization.\nFamiliarity with Docker, CI/CD pipelines, and AWS cloud environments.")
                            .location("Bangalore, KA")
                            .remote(false)
                            .jobType(JobType.FULL_TIME)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(125000.0)
                            .salaryMax(160000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job2 = Job.builder()
                            .title("Full Stack Web Developer")
                            .company("Vercel")
                            .description("Work on building highly interactive, responsive interfaces using React/Next.js and node backend. Ensure fluid micro-interactions and smooth user flows across desktop and mobile devices.")
                            .requirements("Proficient in HTML, CSS (Tailwind), React.js, and TypeScript.\nGood knowledge of RESTful API integration and client-side performance optimization.\nKnowledge of Node.js/Express is a big advantage.")
                            .location("Mumbai, MH")
                            .remote(true)
                            .jobType(JobType.FULL_TIME)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(98000.0)
                            .salaryMax(130000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job3 = Job.builder()
                            .title("Data Science & ML Intern")
                            .company("Meta")
                            .description("Collaborate with data analysts to train predictive machine learning models, parse structured web datasets, and formulate Content Recommendation Algorithms for job matching metrics.")
                            .requirements("Currently pursuing or completed a degree in CSE, Data Science, or Mathematics.\nProficient in Python, NumPy, Pandas, and Scikit-Learn.\nBasic knowledge of SQL and database queries.")
                            .location("Remote")
                            .remote(true)
                            .jobType(JobType.INTERNSHIP)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(50000.0)
                            .salaryMax(70000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job4 = Job.builder()
                            .title("DevOps Cloud Architect")
                            .company("Amazon Web Services")
                            .description("Design AWS/Kubernetes scaling policies, build infrastructure-as-code scripts, monitor system reliability, and improve deployment latency across production setups.")
                            .requirements("Kubernetes CKA/CKAD certification is highly desired.\nStrong experience with Terraform, GitHub Actions, Docker, and shell scripting.\nGood understanding of Linux security controls.")
                            .location("Seattle, WA")
                            .remote(true)
                            .jobType(JobType.CONTRACT)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(145000.0)
                            .salaryMax(190000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job5 = Job.builder()
                            .title("Associate Software Engineer")
                            .company("TCS (Tata Consultancy Services)")
                            .description("Excellent opportunity for fresh graduates to enter corporate IT software development. Work with modern enterprise stacks and get trained by industry leaders on DevOps, Cloud and Full-stack paths.")
                            .requirements("B.E. / B.Tech in CSE, IT, ECE or related engineering branches.\nAcademic projects in Java, Python, SQL, or web technologies.\nGood logical reasoning and problem-solving skills.")
                            .location("Pune, MH")
                            .remote(false)
                            .jobType(JobType.FULL_TIME)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(35000.0)
                            .salaryMax(50000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job6 = Job.builder()
                            .title("Systems Engineer - Cloud Operations")
                            .company("Infosys")
                            .description("Maintain systems stability, deploy code patches, audit application uptime, and coordinate support tickets on cloud services.")
                            .requirements("Degree in Computer Science or IT.\nUnderstanding of networking basics, servers, and Linux commands.\nBasic exposure to AWS/Azure/GCP is preferred.")
                            .location("Hyderabad, TS")
                            .remote(false)
                            .jobType(JobType.FULL_TIME)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(42000.0)
                            .salaryMax(60000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job7 = Job.builder()
                            .title("Cyber Security Analyst")
                            .company("Wipro")
                            .description("Join our security operation center (SOC) team. Analyze event logs, handle incidents, conduct threat hunting, and implement security policies across resources.")
                            .requirements("Knowledge of network security protocols, firewall rules, and penetration testing.\nFamiliarity with Wireshark, Splunk, or similar security tools.\nCertifications like CEH or Security+ are a big plus.")
                            .location("Chennai, TN")
                            .remote(true)
                            .jobType(JobType.FULL_TIME)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(75000.0)
                            .salaryMax(110000.0)
                            .postedBy(recruiter)
                            .build();

                    Job job8 = Job.builder()
                            .title("Backend Developer Intern")
                            .company("Zoho Corporation")
                            .description("Work on Zoho CRM databases and core APIs. Help build scalable data endpoints, write unit tests, and optimize query latency.")
                            .requirements("Excellent coding skills in Java, C++, or Node.js.\nDeep understanding of database index design, normalization, and ACID properties.\nAvailable for a full-time 6 months internship.")
                            .location("Chennai, TN")
                            .remote(false)
                            .jobType(JobType.INTERNSHIP)
                            .status(JobStatus.ACTIVE)
                            .salaryMin(20000.0)
                            .salaryMax(30000.0)
                            .postedBy(recruiter)
                            .build();

                    jobRepository.save(job1);
                    jobRepository.save(job2);
                    jobRepository.save(job3);
                    jobRepository.save(job4);
                    jobRepository.save(job5);
                    jobRepository.save(job6);
                    jobRepository.save(job7);
                    jobRepository.save(job8);
                    log.info("Default CSE & Tech jobs seeded successfully!");
                }
            }
        };
    }

    private Role ensureRole(RoleName name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
    }
}
