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
    public void Takeable_sections_are_returned_correctly() throws Exception{
        Course math1 = new Course("4444444", "MATH1", 3);
        Course math2 = mock(Course.class);
        List<Course> list = List.of(math1, math2);
        Student student = new Student("010101", "ali");
        student.setGrade("13981", math1, 20.0);
        Section section = new Section(math2, "01");

        given(sectionRepository.findAll()).willReturn(List.of(section));
        given(courseRepository.findAll()).willReturn(list);
        given(studentRepository.findByStudentNumber(new StudentNumber("1"))).willReturn(java.util.Optional.of(student));

        when(math2.getId()).thenReturn(1L);
        when(math2.getCredits()).thenReturn(3);
        when(math2.getTitle()).thenReturn("math2");
        when(math2.getCourseNumber()).thenReturn("1");
        when(math2.canBeTakenBy(student)).thenReturn(new ArrayList<>());

        mvc.perform(get("/student/takeableSections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseId", is(1)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
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

        mvc.perform(get("/student/takeableSectionsInStudentMajor/010101")
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
    public void Blocked_students_of_one_student_are_returned_correctly() throws Exception{
        Student student = mock(Student.class);;
        Student blockedStudent1 = new Student("010102", "reza");
        Student blockedStudent2 = new Student("010103", "sara");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getBlocked()).thenReturn(List.of(blockedStudent1, blockedStudent2));
        mvc.perform(get("/student/010101/friends/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")))
                .andExpect(jsonPath("$[1].studentNo", is("010103")))
                .andExpect(jsonPath("$[1].name", is("sara")));
    }


    @Test
    public void Student_with_no_blocked_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);;
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/student/010101/friends/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Unreal_Student_cant_return_its_blocked_friends() throws Exception{
        mvc.perform(get("/student/010101/friends/blocked")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Pending_students_of_one_student_are_returned_correctly() throws Exception{
        Student student = mock(Student.class);;
        Student pendingStudent1 = new Student("010102", "reza");
        Student pendingStudent2 = new Student("010103", "sara");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getPending()).thenReturn(List.of(pendingStudent1, pendingStudent2));
        mvc.perform(get("/student/010101/friends/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")))
                .andExpect(jsonPath("$[1].studentNo", is("010103")))
                .andExpect(jsonPath("$[1].name", is("sara")));
    }

    @Test
    public void Student_with_no_pending_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);;
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/student/010101/friends/pending")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Requested_student_of_one_student_is_returned_correctly() throws Exception{
        Student student = mock(Student.class);;
        Student requestedStudent = new Student("010102", "reza");
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        when(student.getRequested()).thenReturn(List.of(requestedStudent));
        mvc.perform(get("/student/010101/friends/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentNo", is("010102")))
                .andExpect(jsonPath("$[0].name", is("reza")));
    }

    @Test
    public void Student_with_no_requested_friends_returns_empty_list() throws Exception{
        Student student = mock(Student.class);;
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student));
        mvc.perform(get("/student/010101/friends/requested")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Student_can_add_other_student_to_his_friends_list_without_request____its_incorrect() throws Exception{
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(Optional.of(student1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010102"))).willReturn(Optional.of(student2));
        mvc.perform(put("/student/010101/friends/accept")
                .content("010102")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
