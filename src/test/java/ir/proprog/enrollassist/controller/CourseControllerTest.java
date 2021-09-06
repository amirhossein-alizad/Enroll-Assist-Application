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
    private FacultyRepository facultyRepository;
    @MockBean
    private MajorRepository majorRepository;
    private AddCourseService addCourseService;
    private Course course, course1, course2;
    private Major major1, major2;
    private Faculty faculty;
    private final List<Course> courses = new ArrayList<>();

    @BeforeEach
    void SetUp() throws Exception {
        course = new TestCourseBuilder()
                .courseNumber("1412121")
                .title("C4")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        course1 = new TestCourseBuilder()
                .courseNumber("1111111")
                .title("C1")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build();
        course2 = new TestCourseBuilder()
                .courseNumber("2222222")
                .title("C2")
                .credits(3)
                .graduateLevel("Undergraduate")
                .build()
                .withPre(course1);
        courseRepository.saveAll(of(course1, course2));
        courses.addAll(of(course1, course2));
        major1 = mock(Major.class);
        given(major1.getId()).willReturn(10L);
        major2 = mock(Major.class);
        faculty= mock(Faculty.class);
        addCourseService = mock(AddCourseService.class);

        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));
        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(faculty));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(major1));
    }

    @Test
    public void All_courses_are_returned_correctly() throws Exception {
        given(courseRepository.findAll()).willReturn(courses);
        mvc.perform(get("/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$[1].courseTitle", is("C2")));
    }

    @Test
    public void Requested_course_is_returned_correctly() throws Exception {
        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));
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
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(1L), Set.of(10L,11L));
        ObjectMapper Obj = new ObjectMapper();
        given(majorRepository.findById(11L)).willReturn(java.util.Optional.of(major2));

        given(major2.getId()).willReturn(11L);
        given(faculty.getMajors()).willReturn(Set.of(major1, major2));

        mvc.perform(post("/courses/1")
                        .content(Obj.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C4")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("1412121")));
    }


    @Test
    public void Course_cannot_be_added_if_faculty_does_not_exist() throws Exception {
        JSONObject request = new JSONObject();

        mvc.perform(post("/courses/90")
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "Faculty not found"));
    }


    @Test
    public void Course_cannot_be_added_if_major_does_not_exist() throws Exception {
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(), Set.of(45L));

        ExceptionList exceptionList = new ExceptionList();
        exceptionList.addNewException(new Exception("Major with id = 45 was not found."));
        when(addCourseService.getMajors(Set.of(45L), faculty)).thenThrow(exceptionList);
        when(addCourseService.addCourse(courseMajorView,faculty)).thenReturn(course);

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult result =  mvc.perform(post("/courses/1")
                        .content(objectMapper.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Major with id = 45 was not found.\"}");
    }


    @Test
    public void Course_with_duplicate_number_is_not_added_correctly() throws Exception{
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(), Set.of(10L));
        ObjectMapper Obj = new ObjectMapper();

        Course preCourse = new Course("1412121", "C5", 4, "Undergraduate");

        given(courseRepository.findCourseByCourseNumber(new CourseNumber("1412121"))).willReturn(Optional.of(preCourse));

        given(faculty.getMajors()).willReturn(Set.of(major1));
        MvcResult result =  mvc.perform(post("/courses/1")
                        .content(Obj.writeValueAsString(courseMajorView))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Course number already exists.\"}");
    }

    @Test
    public void New_course_is_not_added_if_prerequisite_is_not_found() throws Exception{
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(19L), Set.of(10L));
        ObjectMapper Obj = new ObjectMapper();
        given(faculty.getMajors()).willReturn(Set.of(major1));

        MvcResult result =  mvc.perform(post("/courses/1")
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
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONArray jArray = new JSONArray();
        jArray.put(12);

        request.put("graduateLevel","Undergraduate");
        request.put("courseNumber",courseN);
        request.put("courseCredits", -1);
        request.put("courseTitle", "");
        request.put("prerequisites", jArray);
        request.put("majors", majors);

        Course mockedCourse1 = mock(Course.class);
        Course mockedCourse2 = mock(Course.class);

        given(courseRepository.findById(12L)).willReturn(java.util.Optional.of(mockedCourse1));
        given(courseRepository.findById(13L)).willReturn(java.util.Optional.of(mockedCourse2));
        given(faculty.getMajors()).willReturn(Set.of(major1));
        Set<Course> set1 = Set.of(mockedCourse1);
        Set<Course> set2 = Set.of(mockedCourse2);
        when(mockedCourse1.getPrerequisites()).thenReturn(set2);
        when(mockedCourse2.getPrerequisites()).thenReturn(set1);
        when(mockedCourse1.getTitle()).thenReturn("mockedCourse1");
        MvcResult result =  mvc.perform(post("/courses/1")
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"mockedCourse1 has made a loop in prerequisites.\"," +
                "\"2\":\"Course must have a name.\"," +
                "\"3\":\"Course number cannot be empty.\"," +
                "\"4\":\"Credit must be one of the following values: 0, 1, 2, 3, 4.\"}");
    }
}
