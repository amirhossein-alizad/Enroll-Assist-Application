package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    private Course course1, course2, course3;
    private List<Course> courses = new ArrayList<>();
    @BeforeEach
    void SetUp(){
        course1 = new Course("1", "C1", 3);
        course2 = new Course("2", "C2", 3);
        course2.withPre(course1);
        course3 = new Course("3", "C3", 5);
        courses.addAll(List.of(course1, course2, course3));
    }

    @Test
    public void All_courses_are_returned_correctly() throws Exception{
        given(courseRepository.findAll()).willReturn(courses);
        mvc.perform(get("/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[1].courseTitle", is("C2")))
                .andExpect(jsonPath("$[2].courseCredits", is(5)));
    }

    @Test
    public void Requested_course_is_returned_correctly() throws Exception{
        given(courseRepository.findById(1L)).willReturn(java.util.Optional.of(course1));
        mvc.perform(get("/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber", is("1")))
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
                .andExpect(jsonPath("$.courseNumber", is("2")))
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
}
