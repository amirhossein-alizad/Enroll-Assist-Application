package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseController;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.domain.course.AddCourseService;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.course.CourseNumber;
import ir.proprog.enrollassist.domain.faculty.Faculty;
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
    private Course course1, course2, course3;
    private List<Course> courses = new ArrayList<>();

    @BeforeEach
    void SetUp() throws Exception {
        course1 = new Course("1111111", "C1", 3);
        course2 = new Course("2222222", "C2", 3);
        course2.withPre(course1);
        course3 = new Course("3333333", "C3", 4);
        course2.withPre(course1);
        courseRepository.saveAll(of(course1, course2, course3));
        courses.addAll(of(course1, course2, course3));
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
        mvc.perform(get("/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void New_course_is_added_and_returned_correctly() throws Exception {
        Major m1 = mock(Major.class);
        Major m2 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject request = new JSONObject();
        JSONObject courseN = new JSONObject();
        courseN.put("courseNumber", "1412121");
        JSONArray jArray = new JSONArray();
        jArray.put(1);
        request.put("courseNumber", courseN);
        request.put("courseCredits", 3);
        request.put("courseTitle", "C4");
        request.put("prerequisites", jArray);

        JSONArray majors = new JSONArray();
        majors.put(10);
        majors.put(11);

        request.put("majors", majors);

        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));

        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));
        given(majorRepository.findById(11L)).willReturn(java.util.Optional.of(m2));

        given(m1.getId()).willReturn(10L);
        given(m2.getId()).willReturn(11L);
        given(f1.getMajors()).willReturn(Set.of(m1, m2));

        mvc.perform(post("/courses/addCourse/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C4")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("1412121")));
    }


    @Test
    public void Course_cannot_be_added_if_faculty_does_not_exist() throws Exception {
        JSONObject request = new JSONObject();

        mvc.perform(post("/courses/addCourse/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "Faculty not found"));
    }


    @Test
    public void Course_cannot_be_added_if_major_does_not_exist() throws Exception {
        AddCourseService addCourseService = mock(AddCourseService.class);
        Faculty f1 = mock(Faculty.class);
        Course course = new Course("1212121", "AP", 3);
        CourseMajorView courseMajorView = new CourseMajorView(course, Set.of(), Set.of(10L));

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        ExceptionList exceptionList = new ExceptionList();
        exceptionList.addNewException(new Exception("Major with id = 10 was not found."));
        when(addCourseService.getMajors(Set.of(10L), f1)).thenThrow(exceptionList);
        when(addCourseService.addCourse(courseMajorView,f1)).thenReturn(course);

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult result =  mvc.perform(post("/courses/addCourse/1")
                .content(objectMapper.writeValueAsString(courseMajorView))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Major with id = 10 was not found.\"}");
    }


    @Test
    public void Course_with_duplicate_number_is_not_added_correctly() throws Exception{
        Major m1 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject request = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONObject courseN = new JSONObject();
        courseN.put("courseNumber", "1412121");

        request.put("courseNumber",courseN);
        request.put("courseCredits", 3);
        request.put("courseTitle", "C4");
        request.put("majors", majors);

        Course course = new Course("1412121", "C5", 4);

        given(courseRepository.findCourseByCourseNumber(new CourseNumber("1412121"))).willReturn(Optional.of(course));

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));

        given(m1.getId()).willReturn(10L);
        given(f1.getMajors()).willReturn(Set.of(m1));
        MvcResult result =  mvc.perform(post("/courses/addCourse/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Course number already exists.\"}");
    }

    @Test
    public void New_course_is_not_added_if_prerequisite_is_not_found() throws Exception{
        Major m1 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject courseN = new JSONObject();
        courseN.put("courseNumber", "1412121");
        JSONObject request = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONArray jArray = new JSONArray();
        jArray.put(1);
        request.put("courseNumber",courseN);
        request.put("courseCredits", 3);
        request.put("courseTitle", "C4");
        request.put("prerequisites", jArray);
        request.put("majors", majors);

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        given(m1.getId()).willReturn(10L);
        given(f1.getMajors()).willReturn(Set.of(m1));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));

        MvcResult result =  mvc.perform(post("/courses/addCourse/1")
                            .content(request.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"Course with id = 1 was not found.\"}");
    }

    @Test
    public void New_courses_with_violation_are_returned_correctly() throws Exception{
        Major m1 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject courseN = new JSONObject();
        courseN.put("courseNumber", "");
        JSONObject request = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONArray jArray = new JSONArray();
        jArray.put(1);
        request.put("courseNumber",courseN);
        request.put("courseCredits", -1);
        request.put("courseTitle", "");
        request.put("prerequisites", jArray);
        request.put("majors", majors);

        Course mockedCourse1 = mock(Course.class);
        Course mockedCourse2 = mock(Course.class);

        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(mockedCourse1));
        given(courseRepository.findById(2L)).willReturn(java.util.Optional.of(mockedCourse2));
        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        given(m1.getId()).willReturn(10L);
        given(f1.getMajors()).willReturn(Set.of(m1));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));
        Set<Course> set1 = Set.of(mockedCourse1);
        Set<Course> set2 = Set.of(mockedCourse2);
        when(mockedCourse1.getPrerequisites()).thenReturn(set2);
        when(mockedCourse2.getPrerequisites()).thenReturn(set1);
        when(mockedCourse1.getTitle()).thenReturn("mockedCourse1");
        MvcResult result =  mvc.perform(post("/courses/addCourse/1")
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
