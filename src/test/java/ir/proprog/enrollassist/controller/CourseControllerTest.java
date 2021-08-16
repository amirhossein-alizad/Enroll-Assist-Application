package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.*;
import org.apache.coyote.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
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
        course3 = new Course("3333333", "C3", 5);
        course2.withPre(course1);
        courseRepository.saveAll(of(course1, course2, course3));
        courses.addAll(of(course1, course2, course3));
    }

    @Test
    public void All_courses_are_returned_correctly() throws Exception{
        given(courseRepository.findAll()).willReturn(courses);
        mvc.perform(get("/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].courseNumber", is("1111111")))
                .andExpect(jsonPath("$[1].courseTitle", is("C2")))
                .andExpect(jsonPath("$[2].courseCredits", is(5)));
    }

    @Test
    public void Requested_course_is_returned_correctly() throws Exception{
        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));
        mvc.perform(get("/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber", is("1111111")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseTitle", is("C1")))
                .andExpect(jsonPath("$.prerequisites", hasSize(0)));
    }

    @Test
    public void Requested_with_prerequisite_course_is_returned_correctly() throws Exception{
        given(courseRepository.findById(2L)).willReturn(java.util.Optional.of(course2));
        mvc.perform(get("/courses/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber", is("2222222")))
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
    public void New_course_is_added_and_returned_correctly() throws Exception{
        Major m1 = mock(Major.class);
        Major m2 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject request = new JSONObject();
        JSONObject course = new JSONObject();

        JSONArray jArray = new JSONArray();
        jArray.put(1);
        course.put("courseNumber","1412121");
        course.put("courseCredits", 3);
        course.put("courseTitle", "C4");
        course.put("prerequisites", jArray);

        JSONArray majors = new JSONArray();
        majors.put(10);
        majors.put(11);

        request.put("course", course);
        request.put("majors", majors);

        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));

        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));
        given(majorRepository.findById(11L)).willReturn(java.util.Optional.of(m2));

        given(m1.getId()).willReturn(10L);
        given(m2.getId()).willReturn(11L);
        given(f1.getMajors()).willReturn(Set.of(m1, m2));

        mvc.perform(post("/courses/1")
                        .content(request.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C4")))
                .andExpect(jsonPath("$.courseCredits", is(3)))
                .andExpect(jsonPath("$.courseNumber", is("1412121")));
    }

    @Test
    public void Course_with_duplicate_name_is_not_added_correctly() throws Exception{
        Major m1 = mock(Major.class);
        Faculty f1 = mock(Faculty.class);

        JSONObject request = new JSONObject();
        JSONObject courseJson = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);


        courseJson.put("courseNumber","1412121");
        courseJson.put("courseCredits", 3);
        courseJson.put("courseTitle", "C4");

        request.put("course", courseJson);
        request.put("majors", majors);

        Course course = new Course("1412121", "C5", 4);

        given(courseRepository.findCourseByCourseNumber("1412121")).willReturn(Optional.of(course));

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));

        given(m1.getId()).willReturn(10L);
        given(f1.getMajors()).willReturn(Set.of(m1));
        MvcResult result =  mvc.perform(post("/courses/1")
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

        JSONObject request = new JSONObject();
        JSONObject courseJson = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONArray jArray = new JSONArray();
        jArray.put(1);
        courseJson.put("courseNumber","1412121");
        courseJson.put("courseCredits", 3);
        courseJson.put("courseTitle", "C4");
        courseJson.put("prerequisites", jArray);

        request.put("course", courseJson);
        request.put("majors", majors);

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.of(f1));
        given(m1.getId()).willReturn(10L);
        given(f1.getMajors()).willReturn(Set.of(m1));
        given(majorRepository.findById(10L)).willReturn(java.util.Optional.of(m1));

        MvcResult result =  mvc.perform(post("/courses/1")
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

        JSONObject request = new JSONObject();
        JSONObject courseJson = new JSONObject();
        JSONArray majors = new JSONArray();
        majors.put(10);

        JSONArray jArray = new JSONArray();
        jArray.put(1);
        courseJson.put("courseNumber","");
        courseJson.put("courseCredits", -1);
        courseJson.put("courseTitle", "");
        courseJson.put("prerequisites", jArray);

        request.put("course", courseJson);
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
        MvcResult result =  mvc.perform(post("/courses/1")
                            .content(request.toString())
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isBadRequest())
                            .andReturn();
        String content = result.getResponse().getErrorMessage();
        assertEquals(content, "{\"1\":\"mockedCourse1 has made a loop in prerequisites.\"," +
                                    "\"2\":\"Course number cannot be empty.\"," +
                                    "\"3\":\"Course must have a name.\"," +
                                    "\"4\":\"Course credit units cannot be negative.\"}");
    }
}
