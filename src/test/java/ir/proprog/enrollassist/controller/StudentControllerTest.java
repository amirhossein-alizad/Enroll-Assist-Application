package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.student.StudentController;
import ir.proprog.enrollassist.domain.*;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.repository.*;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @MockBean
    private MajorRepository majorRepository;

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
        request.put("majorId", 12L);
        Major major = mock(Major.class);
        Student student = new Student("81818181", "Mehrnaz");
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willReturn(Optional.of(student));
        given(this.majorRepository.findById(12L)).willReturn(Optional.of(major));
        mvc.perform(post("/student")
               .content(request.toString())
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void Valid_student_is_added_correctly() throws Exception {
        JSONObject request = new JSONObject();
        Major major = mock(Major.class);
        request.put("studentNo", "81818181");
        request.put("name", "Sara");
        request.put("majorId", 12L);
        given(this.studentRepository.findByStudentNumber(new StudentNumber("81818181"))).willReturn(Optional.empty());
        given(this.majorRepository.findById(12L)).willReturn(Optional.of(major));
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
        mvc.perform(get("/student/1/takeableByMajor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void Takeable_sections_by_major_are_returned_correctly() throws Exception{
        Course math1 = new Course("4444444", "MATH1", 3);
        Course ap = new Course("4444004", "AP", 3);
        Course math2 = new Course("4666644", "MATH2", 3).withPre(math1);

        Major cs = new Major("1", "CS");
        cs.addCourse(math1, math2);

        Student student = new Student("010101", "ali", cs);
        student.setGrade("13981", math1, 20.0);

        Section math2_1 = new Section(math2, "01");
        Section math2_2 = new Section(math2, "02");
        Section math1_1 = new Section(math1, "01");
        Section ap_1 = new Section(ap, "01");


        given(sectionRepository.findAll()).willReturn(List.of(math1_1, math2_1, math2_2, ap_1));
        given(studentRepository.findByStudentNumber(new StudentNumber("010101"))).willReturn(java.util.Optional.of(student));

        mvc.perform(get("/student/010101/takeableByMajor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber", is("4666644")))
                .andExpect(jsonPath("$[0].courseTitle", is("MATH2")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)))
                .andExpect(jsonPath("$[1].sectionNo", is("02")))
                .andExpect(jsonPath("$[1].courseNumber.courseNumber", is("4666644")))
                .andExpect(jsonPath("$[1].courseTitle", is("MATH2")))
                .andExpect(jsonPath("$[1].courseCredits", is(3)));

    }


}
