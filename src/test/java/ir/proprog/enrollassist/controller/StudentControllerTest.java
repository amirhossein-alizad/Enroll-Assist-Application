package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @MockBean
    private SectionRepository sectionRepository;

    @Test
    public void Student_that_doesnt_exist_is_not_found() throws Exception {
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        mvc.perform(get("/student/81818181")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Requested_student_is_returned_correctly() throws Exception {
        Student student = new Student("81818181", "Mehrnaz");
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willReturn(Optional.of(student));
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
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willReturn(Optional.of(student));
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
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willReturn(Optional.empty());
        mvc.perform(post("/student")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Sara")))
                .andExpect(jsonPath("$.studentNo", is("81818181")));
    }

//    @Test
//    public void Student_with_empty_studentNo_is_not_added_correctly() throws Exception {
//        JSONObject request = new JSONObject();
//        request.put("studentNo", "");
//        request.put("name", "Sara");
//        given(this.studentRepository.findByStudentNumber("")).willReturn(Optional.empty());
//        mvc.perform(post("/student")
//                .content(request.toString())
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }


    @Test
    public void Takeable_sections_is_not_returned_if_student_is_not_found() throws Exception{
        mvc.perform(get("/student/1/takeable")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void Takeable_sections_by_major_are_returned_correctly() throws Exception{
        Student student = new Student("010101", "ali");
        Course math1 = new Course("4444444", "MATH1", 3);
        Course ap = new Course("4444004", "AP", 3);
        Course math2 = new Course("4666644", "MATH2", 3).withPre(math1);
        student.setGrade("13981", math1, 20.0);
        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        Section math1_1 = new Section(math1, "01");
        Section ap_1 = new Section(ap, "01");

        Major cs = new Major("1", "CS");
        cs.addCourse(math1, math2);
        student.setMajor(cs);

        given(sectionRepository.findAll()).willReturn(List.of(math1_1, math2_1, math2_2, ap_1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));

        mvc.perform(get("/student/010101/takeable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseNumber", is("4666644")))
                .andExpect(jsonPath("$[0].courseTitle", is("MATH2")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)))
                .andExpect(jsonPath("$[1].sectionNo", is("02")))
                .andExpect(jsonPath("$[1].courseNumber", is("4666644")))
                .andExpect(jsonPath("$[1].courseTitle", is("MATH2")))
                .andExpect(jsonPath("$[1].courseCredits", is(3)));

    }

    @Test
    public void Friendship_requests_are_sent_correctly() throws Exception{
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(put("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_requested_student_does_not_exist() throws Exception{
        Student student1 = new Student("010101", "bob");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        mvc.perform(put("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 111111 not found"));
    }

    @Test
    public void Friendship_requests_cannot_be_sent_if_sender_does_not_exist() throws Exception {
        mvc.perform(put("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Friendships_are_removed_correctly() throws Exception {
        Student student1 = new Student("010101", "bob");
        Student student2 = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student2));
        mvc.perform(delete("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_student_does_not_exist() throws Exception {
        Student student = new Student("111111", "bill");
        given(studentRepository.findByStudentNumber(new StudentNumber("111111"))).willReturn(java.util.Optional.of(student));
        mvc.perform(delete("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 010101 not found"));
    }

    @Test
    public void Friendships_cannot_be_removed_if_requested_friend_does_not_exist() throws Exception {
        Student student = new Student("010101", "bob");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));
        mvc.perform(delete("/student/010101/friends")
                .content("111111")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "requested Student with id 111111 not found"));
    }
}
