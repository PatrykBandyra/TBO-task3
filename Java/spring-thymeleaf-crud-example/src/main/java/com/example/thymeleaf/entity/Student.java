package com.example.thymeleaf.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Entity
@Table(name = "student")
@EqualsAndHashCode(of = {"id"})
public class Student {
    @Transient
    private static final Logger logger = LoggerFactory.getLogger(Student.class);

    @Id
    @Setter
    private String id;

    private String name;
    private String email;
    private LocalDate birthday;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private Address address;

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void setBirthday(LocalDate birthday) {
        validateBirthday(birthday);
        this.birthday = birthday;
    }

    @PrePersist
    private void prePersist() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        logger.info("Created user {}", this);
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", address=" + address +
                '}';
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new StudentValidationException("Name is blank");
        }
        if (!hasLengthBetween(name, StudentValidationConstants.NAME_MIN_LENGTH, StudentValidationConstants.NAME_MAX_LENGTH)) {
            throw new StudentValidationException(String.format(
                    "Name length must be between %d and %d",
                    StudentValidationConstants.NAME_MIN_LENGTH, StudentValidationConstants.NAME_MAX_LENGTH
            ));
        }
    }

    private static void validateEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new StudentValidationException("Email is blank");
        }
        if (!hasLengthBetween(email, StudentValidationConstants.EMAIL_MIN_LENGTH, StudentValidationConstants.EMAIL_MAX_LENGTH)) {
            throw new StudentValidationException(String.format(
                    "Email length must be between %d and %d",
                    StudentValidationConstants.EMAIL_MIN_LENGTH, StudentValidationConstants.EMAIL_MAX_LENGTH
            ));
        }
        if (!email.matches(StudentValidationConstants.OWASP_EMAIL_REGEX)) {
            throw new StudentValidationException("Email format is invalid");
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (Objects.isNull(birthday)) {
            throw new StudentValidationException("Birthday is null");
        }
        if (birthday.isAfter(LocalDate.now()) ||
                birthday.isBefore(LocalDate.now().minusYears(StudentValidationConstants.MAX_STUDENT_AGE))) {
            throw new StudentValidationException("Student is too old");
        }
    }

    private static boolean hasLengthBetween(@NonNull String value, int min, int max) {
        return value.length() >= min && value.length() <= max;
    }

    static final class StudentValidationException extends RuntimeException {
        public StudentValidationException(String message) {
            super(message);
        }
    }

    private static final class StudentValidationConstants {
        private static final String OWASP_EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        private static final int NAME_MIN_LENGTH = 2;
        private static final int NAME_MAX_LENGTH = 50;
        private static final int EMAIL_MIN_LENGTH = 10;
        private static final int EMAIL_MAX_LENGTH = 50;
        private static final int MAX_STUDENT_AGE = 65;  // Older can go to the University of the Third Age
    }
}
