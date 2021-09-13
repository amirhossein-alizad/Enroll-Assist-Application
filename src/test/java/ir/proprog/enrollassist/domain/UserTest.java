package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.domain.utils.TestStudentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    User user;
    Student student1, student2;
    TestStudentBuilder testStudentBuilder;
    @BeforeEach
    void setUp() throws Exception{
        user = new User("user");
        testStudentBuilder = new TestStudentBuilder();
        student1 = testStudentBuilder.build();
        user.addStudent(student1);
    }

    @Test
    void User_name_can_not_be_empty(){
        Throwable error = assertThrows(Exception.class, () ->
                new User(""));
        assertEquals(error.getMessage().toString(), "Name can not be empty.");
    }

    @Test
    void User_name_can_not_be_null(){
        Throwable error = assertThrows(Exception.class, () ->
                new User(null));
        assertEquals(error.getMessage().toString(), "Name can not be null.");
    }

    @Test
    void Students_with_different_names_are_not_added_to_users_list() throws Exception{
        student2 = testStudentBuilder
                .withName("amir")
                .build();
        Throwable error = assertThrows(Exception.class, () ->
                user.addStudent(student2));
        assertEquals(error.getMessage().toString(), "Student name can not be different.");
    }

    @Test
    void Students_with_the_same_name_are_added_to_users_list_correctly() throws Exception{
        student2 = testStudentBuilder
                .withStudentNumber("810197546")
                .build();
        user.addStudent(student2);
        assertThat(user.getStudents())
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(student1, student2);
    }
}
