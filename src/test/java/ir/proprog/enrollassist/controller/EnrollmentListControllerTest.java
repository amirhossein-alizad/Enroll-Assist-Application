package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListController;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.domain.EnrollmentRules.CourseRequestedTwice;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import ir.proprog.enrollassist.repository.StudentRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrollmentListController.class)
public class EnrollmentListControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @MockBean
    private StudentRepository studentRepository;

    EnrollmentList list1;
    private Student std;
    private Course course;

    @BeforeEach
    public void setUp() throws ExceptionList {
        std = new Student("00000", "gina", mock(Major.class), "Undergraduate");
        list1 = new EnrollmentList("list1", std);
        course = new Course("1111111", "C1", 3, "Undergraduate");

        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        given(studentRepository.findByStudentNumber(new StudentNumber("00000"))).willReturn(Optional.of(std));
    }

    @Test
    public void All_lists_are_returned_correctly() throws Exception {
        given(enrollmentListRepository.findAll()).willReturn(List.of(list1));
        mvc.perform(get("/lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].enrollmentListName", is(this.list1.getListName())));
    }

    @Test
    public void Requested_list_is_returned_correctly() throws Exception {
        mvc.perform(get("/lists/12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is(this.list1.getListName())));
    }

    @Test
    public void New_list_is_added_correctly() throws Exception {
        EnrollmentList newEnrollmentList = new EnrollmentList("new_list", std);
        ObjectMapper Obj = new ObjectMapper();
        mvc.perform(post("/lists/00000")
                .content(Obj.writeValueAsString(new EnrollmentListView(newEnrollmentList)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("new_list")));
        verify(enrollmentListRepository, times(1)).save(newEnrollmentList);
    }

    @Test
    public void New_list_cannot_be_added_if_owners_student_number_does_not_exist() throws Exception {
        JSONObject req = new JSONObject();
        req.put("enrollmentListName", "new_list");
        mvc.perform(post("/lists/00001")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void List_that_doesnt_exist_is_not_found() throws Exception {
        mvc.perform(get("/lists/18")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void List_with_no_sections_is_returned_correctly() throws Exception {
        mvc.perform(get("/lists/12/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void Untitled_enrollment_lists_cannot_be_added() throws Exception {
        JSONObject req = new JSONObject();
        req.put("enrollmentListName", "");
        mvc.perform(post("/lists/00000")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals(HttpStatus.BAD_REQUEST + " \"EnrollmentList must have a name.\"", result.getResolvedException().getMessage()));
    }

    @Test
    public void Enrollment_lists_with_same_name_cannot_be_added() throws Exception {
        EnrollmentListView enrollmentListView = new EnrollmentListView(new EnrollmentList("Mahsa's List", std));
        given(studentRepository.findAllListsForStudent("00000")).willReturn(List.of(enrollmentListView));
        JSONObject req = new JSONObject();
        req.put("enrollmentListName", "Mahsa's List");
        mvc.perform(post("/lists/00000")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException))
                .andExpect(result -> assertEquals(HttpStatus.BAD_REQUEST + " \"EnrollmentList with name Mahsa's List already exists.\"", result.getResolvedException().getMessage()));
    }

    @Test
    public void EnrollmentList_check_is_not_found_if_it_doesnt_exist() throws Exception{
        mvc.perform(get("/lists/1/check")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void All_sections_of_list_are_returned_correctly() throws Exception {
        this.list1.addSections(new Section(course, "01"));
        mvc.perform(get("/lists/12/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)));
    }

    @Test
    public void No_violations_are_returned_for_a_valid_list() throws Exception {
        EnrollmentList enrollmentList = mock(EnrollmentList.class);
        given(enrollmentListRepository.findById(15L)).willReturn(Optional.of(enrollmentList));
        when(enrollmentList.checkEnrollmentRules()).thenReturn(List.of());
        mvc.perform(get("/lists/15/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(0)));
    }

    @Test
    public void Violations_of_unacceptable_list_are_returned_correctly() throws Exception {
        EnrollmentList enrollmentList = mock(EnrollmentList.class);
        given(enrollmentListRepository.findById(15L)).willReturn(Optional.of(enrollmentList));
        CourseRequestedTwice violation = new CourseRequestedTwice(course);
        when(enrollmentList.checkEnrollmentRules()).thenReturn(List.of(violation));
        mvc.perform(get("/lists/15/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages[0]", is(violation.toString())));
    }

    @Test
    public void Section_is_added_correctly_to_the_requested_list() throws Exception {
        given(sectionRepository.findById(2L)).willReturn(Optional.of(new Section(course, "02")));
        mvc.perform(MockMvcRequestBuilders.put("/lists/12/sections/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(enrollmentListRepository, times(1)).save(list1);
    }

    @Test
    public void Section_is_removed_correctly_from_the_requested_list() throws Exception {
        given(sectionRepository.findById(2L)).willReturn(Optional.of(new Section(course, "02")));
        mvc.perform(MockMvcRequestBuilders.delete("/lists/12/sections/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(enrollmentListRepository, times(1)).save(list1);
    }

    @Test
    public void Section_cannot_be_removed_from_list_if_requested_list_does_not_exist() throws Exception {
        given(sectionRepository.findById(2L)).willReturn(Optional.of(new Section(course, "02")));
        mvc.perform(MockMvcRequestBuilders.delete("/lists/10/sections/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Section_cannot_be_removed_if_it_does_not_exist() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete("/lists/12/sections/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Enrollment_list_is_emptied_correctly() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/lists/12/clear")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(enrollmentListRepository, times(1)).save(list1);
    }
}
