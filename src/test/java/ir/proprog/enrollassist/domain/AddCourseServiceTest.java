package ir.proprog.enrollassist.domain;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.course.AddCourseService;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class AddCourseServiceTest {
    private AddCourseService addCourseService;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private ProgramRepository programRepository;

    @BeforeEach
    public void setUp() throws ExceptionList {
        Course course1 = new Course("1100112", "DS", 3, "Undergraduate");
        Course course2 = new Course("1100113", "DA", 3, "Undergraduate");
        given(courseRepository.findAll()).willReturn(List.of(course1, course2));
        Major ce = new Major("1", "CE");
        Major ee = new Major("2", "EE");
        Program ceMajor = new Program(ce, "Undergraduate", 140, 140, "Major");
        Program eeMajor = new Program(ee, "Undergraduate", 140, 140, "Major");
//        ce.addCourse(course1);
//        ee.addCourse(course2);
        given(programRepository.findAll()).willReturn(List.of(ceMajor, eeMajor));
        given(programRepository.findById(67L)).willReturn(Optional.of(eeMajor));
        this.addCourseService = new AddCourseService(courseRepository, programRepository);
    }


    @Test
    public void With_valid_input_course_is_added_correctly() throws Exception{
        Course course = new Course("1100110", "OS", 3, "Undergraduate");
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
        Course course = new Course("9999909", "OS", 3, "Undergraduate");
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
        Long id1 = 68L;
        String error = "";
        try {
            addCourseService.getPrograms(Set.of(id1));
        } catch (ExceptionList exceptionList) {
            error = exceptionList.toString();
        }
        assertEquals(error, "{\"1\":\"Program with id = 68 was not found.\"}");
    }
}
