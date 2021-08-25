package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.CourseMajorView;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.util.TestDataInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AddCourseServiceTest {
    @Autowired
    private AddCourseService addCourseService;
    @Autowired
    private TestDataInitializer testDataInitializer;

    @BeforeEach
    public void populate () throws Exception {
        testDataInitializer.populate();
    }

    @AfterEach
    public void cleanUp() {
        testDataInitializer.deleteAll();
    }

    @Test
    public void With_valid_course_is_created_correctly() throws Exception{
        Course course = new Course("1100110", "DS", 3);
        CourseMajorView courseMajorView = new CourseMajorView(course, Collections.emptySet(), Collections.emptySet());
        try {
            Course newCourse = addCourseService.addCourse(courseMajorView);
            assertEquals(newCourse, course);
        } catch (ExceptionList exceptionList) {}
    }


}
