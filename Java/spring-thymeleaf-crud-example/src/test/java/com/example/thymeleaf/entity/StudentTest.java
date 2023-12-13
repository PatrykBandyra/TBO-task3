package com.example.thymeleaf.entity;

import com.example.thymeleaf.entity.Student.StudentValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class StudentTest {

    @Nested
    class StudentNameTest {
        @DisplayName("Should not throw error when setting valid name on student")
        @ParameterizedTest
        @MethodSource("validNames")
        void shouldNotThrowErrorWhenSettingValidNameOnStudent(String name) {
            Student s = new Student();
            assertDoesNotThrow(() -> s.setName(name));
            assertEquals(name, s.getName());
        }

        @DisplayName("Should throw error when setting invalid name on student")
        @ParameterizedTest
        @MethodSource("invalidNames")
        void shouldThrowErrorWhenSettingValidNameOnStudent(String name) {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setName(name));
            assertNull(s.getName());
        }

        private static List<String> validNames() {
            return List.of("John", "Jo", "JsoafmsdaofnjsdanfisadfdsJsoafmsdaofnjsdanfisadfds");
        }

        private static List<String> invalidNames() {
            return List.of("", " ", "   ", "J", "JsoafmsdaofnjsdanfisadfdsJsoafmsdaofnjsdanfisadfdsfasdfsdaf");
        }
    }

    @Nested
    class StudentEmailTest {
        @DisplayName("Should not throw error when setting valid email on student")
        @ParameterizedTest
        @MethodSource("validEmails")
        void shouldNotThrowErrorWhenSettingValidEmailOnStudent(String email) {
            Student s = new Student();
            assertDoesNotThrow(() -> s.setEmail(email));
            assertEquals(email, s.getEmail());
        }

        @DisplayName("Should throw error when setting invalid email on student")
        @ParameterizedTest
        @MethodSource("invalidEmails")
        void shouldThrowErrorWhenSettingValidEmailOnStudent(String email) {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setEmail(email));
            assertNull(s.getEmail());
        }

        private static List<String> validEmails() {
            return List.of(
                    "john.doe@example.com", "jane_smith123@gmail.com", "info@companywebsite.com",
                    "support@techcompany.net", "myname1234@yahoo.co.uk", "sales_department@business.co.in",
                    "firstname.lastname@organization.org", "user1234@emailprovider.com",
                    "marketing-team@consultancyfirm.net", "contact_us@startup.io"
            );
        }

        private static List<String> invalidEmails() {
            return List.of(
                    "john.doe@example", "jane_smith123@gmailcom", "@companywebsite.com", "support@techcompany",
                    "myname1234@yahoo", "sales_department@.co.in", "firstname.lastname@organization",
                    "user1234emailprovider.com", "marketing-team@consultancyfirm.", "contact_us@startupio",
                    "contact us@startupio.com"
            );
        }
    }

    @Nested
    class StudentBirthdayTest {

        @Test
        @DisplayName("Should not throw error when setting past birthday on student")
        void shouldNotThrowErrorWhenSettingPastBirthdayOnStudent() {
            Student s = new Student();
            LocalDate birthday = LocalDate.now().minusDays(20);
            assertDoesNotThrow(() -> s.setBirthday(birthday));
            assertEquals(birthday, s.getBirthday());
        }

        @Test
        @DisplayName("Should throw error when setting future birthday on student")
        void shouldThrowErrorWhenSettingFutureBirthdayOnStudent() {
            Student s = new Student();
            LocalDate birthday = LocalDate.now().plusDays(20);
            assertThrows(StudentValidationException.class, () -> s.setBirthday(birthday));
            assertNull(s.getBirthday());
        }
    }

    @Nested
    class StudentSqlInjectionTest {
        @Test
        @DisplayName("Should throw error when setting name containing SQL injection on student")
        void shouldThrowErrorWhenSettingNameContainingSqlInjectionOnStudent() {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setName(SQL_INJECTION_PAYLOAD));
            assertNull(s.getName());
        }

        private final String SQL_INJECTION_PAYLOAD = "' OR 1=1 --";
    }

    @Nested
    class StudentJavaScriptInjectionTest {
        @Test
        @DisplayName("Should throw error when setting name containing JavaScript injection")
        void shouldThrowErrorWhenSettingNameContainingJavaScriptInjectionOnStudent() {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setName(JAVASCRIPT_INJECTION_PAYLOAD));
            assertNull(s.getName());
        }

        private final String JAVASCRIPT_INJECTION_PAYLOAD = "<script>alert(\"Hello\");</script>";
    }

    @Nested
    class StudentExtremeTest {

        @DisplayName("Should throw error when setting name with large value")
        @ParameterizedTest
        @MethodSource("largeNames")
        void shouldThrowErrorWhenSettingNameWithLargeValue(String name) {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setName(name));
            assertNull(s.getName());
        }

        @DisplayName("Should throw error when setting email with large value")
        @ParameterizedTest
        @MethodSource("largeEmails")
        void shouldThrowErrorWhenSettingEmailWithLargeValue(String email) {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setEmail(email));
            assertNull(s.getEmail());
        }

        @DisplayName("Should throw error when setting birthday with very far date")
        @ParameterizedTest
        @MethodSource("largeDates")
        void shouldThrowErrorWhenSettingBirthdayWithVeryFarDate(LocalDate birthday) {
            Student s = new Student();
            assertThrows(StudentValidationException.class, () -> s.setBirthday(birthday));
            assertNull(s.getBirthday());
        }

        private static List<String> largeNames() {
            return List.of(
                    "X".repeat(1_000),
                    "X".repeat(10_000),
                    "X".repeat(100_000),
                    "X".repeat(1_000_000)
            );
        }

        private static List<String> largeEmails() {
            return largeNames().stream().map(name -> name + "@gmail.com").toList();
        }

        private static List<LocalDate> largeDates() {
            return List.of(
                    LocalDate.now().plusYears(1_000),
                    LocalDate.now().plusYears(10_000),
                    LocalDate.now().plusYears(100_000),
                    LocalDate.now().minusYears(1_000),
                    LocalDate.now().minusYears(10_000),
                    LocalDate.now().minusYears(100_000)
            );
        }
    }
}