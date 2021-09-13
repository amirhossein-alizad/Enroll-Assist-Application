package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseController;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.controller.course.CourseView;
import ir.proprog.enrollassist.domain.course.AddCourseService;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.course.CourseNumber;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.utils.TestCourseBuilder;
import ir.proprog.enrollassist.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CourseController.class, AddCourseService.class})
public class CourseControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private ProgramRepository programRepository;
    private TestCourseBuilder testCourseBuilder = new TestCourseBuilder();
    private AddCourseService addCourseService;
    private Course course1, course2, course3;
    private Major major1, major2;
    private Program program1, program2;
    private final List<Course> courses = new ArrayList<>();

    @BeforeEach
    void SetUp() throws Exception {
        course1 = testCourseBuilder
                .build();
        course2 = testCourseBuilder
                .title("C2")
                .courseNumber("2222222")
                .build()
                .withPre(course1);
        course3 = testCourseBuilder
                .title("C3")
                .courseNumber("3333333")
                .credits(4)
                .build()
                .withPre(course1);
        courseRepository.saveAll(of(course1, course2, course3));
        courses.addAll(of(course1, course2, course3));
        major1 = mock(Major.class);
        given(major1.getId()).willReturn(10L);
        major2 = mock(Major.class);
        program1 = new Program(major1, "Undergraduate", 70, 80, "Major");
        program2 = new Program(major2, "Undergraduate", 78, 80, "Major");
        addCourseService = mock(AddCourseService.class);

        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));
        given(programRepository.findById(10L)).willReturn(java.util.Optional.of(program1));
    }

    @Test
    public void All_courses_are_returned_correctly() throws Exception {
        given(courseRepository.findAll()).willReturn(courses);
        mvc.perform(get("/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$[1].courseTitle", is("C2")))
                .andExpect(jsonPath("$[2].courseCredits", is(4)));
    }

    @Test
    public void Requested_course_is_returned_correctly() throws Exception {
        mvc.perform(get("/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseTitle", is("C1")))
                .andExpect(jsonPath("$.prerequisites", hasSize(0)));
    }

    @Test
    public void Requested_with_prerequisite_course_is_returned_correctly() throws Exception {
        given(courseRepository.findById(2L)).willReturn(java.util.Optional.of(course2));
        mvc.perform(get("/courses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("2222222")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseTitle", is("C2")))
                .andExpect(jsonPath("$.prerequisites", hasSize(1)));
    }

    @Test
    public void Course_that_doesnt_exist_is_not_found() throws Exception {
        mvc.perform(get("/courses/34")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void New_course_is_added_and_returned_correctly() throws Exception {
        Course course = new Course("1412121", "C4", 3, "Undergraduate");
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(1L), Set.of(10L,11L));
        ObjectMapper Obj = new ObjectMapper();
        given(programRepository.findById(11L)).willReturn(java.util.Optional.of(program2));

        mvc.perform(post("/courses")
                        .content(Obj.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C4")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("1412121")));
    }


    @Test
    public void Course_cannot_be_added_if_major_does_not_exist() throws Exception {
        Course course = new Course("1212121", "AP", 3, "Undergraduate");
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(), Set.of(45L));

        ExceptionList exceptionList = new ExceptionList();
        exceptionList.addNewException(new Exception("Program with id = 45 was not found."));
        when(addCourseService.getPrograms(Set.of(45L))).thenThrow(exceptionList);
        when(addCourseService.addCourse(courseMajorView)).thenReturn(course);

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult result =  mvc.perform(post("/courses")
                        .content(objectMapper.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Program with id = 45 was not found.\"}");
    }


    @Test
    public void Course_with_duplicate_number_is_not_added_correctly() throws Exception{
        Course course = new Course("1412121", "C4", 3, "Undergraduate");
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(), Set.of(10L));
        ObjectMapper Obj = new ObjectMapper();

        Course preCourse = new Course("1412121", "C5", 4, "Undergraduate");

        given(courseRepository.findCourseByCourseNumber(new CourseNumber("1412121"))).willReturn(Optional.of(preCourse));

        MvcResult result =  mvc.perform(post("/courses")
                        .content(Obj.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Course number already exists.\"}");
    }

    @Test
    public void New_course_is_not_added_if_prerequisite_is_not_found() throws Exception{
        Course course = new Course("1412121", "C4", 3, "Undergraduate");
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(19L), Set.of(10L));
        ObjectMapper Obj = new ObjectMapper();

        MvcResult result =  mvc.perform(post("/courses")
                        .content(Obj.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Course with id = 19 was not found.\"}");
    }

    @Test
    public void New_courses_with_violation_are_returned_correctly() throws Exception{
        JSONObject courseN = new JSONObject();
        courseN.put("courseNumber", "");
        JSONObject request = new JSONObject();
        JSONArray programs = new JSONArray();
        programs.put(10L);

        JSONArray jArray = new JSONArray();
        jArray.put(12);

        request.put("graduateLevel","Undergraduate");
        request.put("courseNumber",courseN);
        request.put("courseCredits", -1);
        request.put("courseTitle", "");
        request.put("prerequisites", jArray);
        request.put("programs", programs);

        given(courseRepository.findById(12L)).willReturn(java.util.Optional.of(course1));
        course1.withPre(course2);
        course2.withPre(course1);
        MvcResult result =  mvc.perform(post("/courses")
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"C1 has made a loop in prerequisites.\"," +
                "\"2\":\"Course must have a name.\"," +
                "\"3\":\"Course number cannot be empty.\"," +
                "\"4\":\"Credit must be one of the following values: 0, 1, 2, 3, 4.\"}");
    }
}
