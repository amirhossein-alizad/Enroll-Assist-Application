package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void Student_that_doesnt_exist_is_not_found() throws Exception {
        given(this.studentRepository.findByStudentNumber("81818181")).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        mvc.perform(get("/student/81818181")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Requested_student_is_returned_correctly() throws Exception {
        Student student = new Student("81818181", "Mehrnaz");
        given(this.studentRepository.findByStudentNumber("81818181")).willReturn(Optional.of(student));
        mvc.perform(get("/student/81818181")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mehrnaz")))
                .andExpect(jsonPath("$.studentNo", is("81818181")));
    }

    @Test
    public void One_student_is_not_added_again() throws Exception {
        JSONObject request = new JSONObject();
        request.put("studentNo", "81818181");
        request.put("name", "Sara");
        Student student = new Student("81818181", "Mehrnaz");
        given(this.studentRepository.findByStudentNumber("81818181")).willReturn(Optional.of(student));
        mvc.perform(post("/student")
               .content(request.toString())
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void Invalid_student_is_not_added_correctly() throws Exception {
        JSONObject request = new JSONObject();
        request.put("studentNo", "81818181");
        request.put("name", "Sara");
        given(this.studentRepository.findByStudentNumber("81818181")).willReturn(Optional.empty());
        mvc.perform(post("/student")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sara")))
                .andExpect(jsonPath("$.studentNo", is("81818181")));
    }

    @Test
    public void Student_with_empty_studentNo_is_not_added_correctly() throws Exception {
        JSONObject request = new JSONObject();
        request.put("studentNo", "");
        request.put("name", "Sara");
        given(this.studentRepository.findByStudentNumber("")).willReturn(Optional.empty());
        mvc.perform(post("/student")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Takeable_sections_are_returned_correctly() throws Exception{
        Course math1 = new Course("4444444", "MATH1", 3);
        Course math2 = mock(Course.class);
        List<Course> list = List.of(math1, math2);
        Student mockStudent = mock(Student.class);

        given(courseRepository.findAll()).willReturn(list);
        given(studentRepository.findByStudentNumber("1")).willReturn(java.util.Optional.of(mockStudent));

        when(mockStudent.getPassedCourses()).thenReturn(List.of(math1));
        when(mockStudent.hasPassed(math2)).thenReturn(false);
        when(mockStudent.hasPassed(math1)).thenReturn(true);

        when(math2.getId()).thenReturn(1L);
        when(math2.getCredits()).thenReturn(3);
        when(math2.getTitle()).thenReturn("math2");
        when(math2.getCourseNumber()).thenReturn("1");
        when(math2.canBeTakenBy(mockStudent)).thenReturn(new ArrayList<>());

        mvc.perform(get("/student/takeableSections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseId", is(1)))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].courseTitle", is("math2")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)));

    }


    @Test
    public void Takeable_sections_is_not_returned_if_student_is_not_found() throws Exception{
        mvc.perform(get("/student/takeableSections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
