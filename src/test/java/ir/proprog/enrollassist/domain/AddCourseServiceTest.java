package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.CourseMajorView;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AddCourseServiceTest {
    private AddCourseService addCourseService;
    Faculty faculty;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MajorRepository majorRepository;

    @BeforeEach
    public void setUp() throws ExceptionList {
        Course course1 = new Course("1100112", "DS", 3);
        Course course2 = new Course("1100113", "DA", 3);
        courseRepository.save(course2);
        courseRepository.save(course1);
        Major ce = new Major("1", "CE");
        Major ee = new Major("2", "EE");
        ce.addCourse(course1);
        ee.addCourse(course2);
        majorRepository.save(ee);
        majorRepository.save(ce);
        faculty = new Faculty("ece");
        faculty.addMajor(ce);
        this.addCourseService = new AddCourseService(courseRepository, majorRepository);
    }


    @Test
    public void With_valid_input_course_is_added_correctly() throws Exception{
        Course course = new Course("1100110", "OS", 3);
        CourseMajorView courseMajorView = new CourseMajorView(course, Collections.emptySet(), Collections.emptySet());
        String error = "";
        try {
            Course newCourse = addCourseService.addCourse(courseMajorView);
            assertEquals(newCourse, course);
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "");
    }

    @Test
    public void Course_is_not_added_correctly_with_invalid_prerequisites() throws Exception{
        Course course = new Course("9999909", "OS", 3);
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(21L), Collections.emptySet());
        String error = "";
        try {
            Course newCourse = addCourseService.addCourse(courseMajorView);
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Course with id = 21 was not found.\"}");
    }

    @Test
    public void Unreal_major_is_not_returned_correctly() {
        Long id1 = 67L;
        String error = "";
        try {
            addCourseService.getMajors(Set.of(id1), faculty);
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Major with id = 67 was not found.\"}");
    }

    @Test
    public void Major_that_is_not_in_faculty_is_not_returned_correctly() {
        Long id1 = 67L ;
        for (Major m: majorRepository.findAll())
            if (m.getMajorName().equals("EE"))
                id1 = m.getId();

        String error = "";
        try {
            addCourseService.getMajors(Set.of(id1), faculty);
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Major with id = " + id1 + " not belong to this faculty.\"}");
    }


}
