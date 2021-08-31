package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListController;
import ir.proprog.enrollassist.controller.enrollmentList.EnrollmentListView;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.domain.studyRecord.Grade;
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
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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

    List<EnrollmentList> lists;
    EnrollmentList list1, list2;
    private Section S1, S2, S3, S4;
    private Student std;

    @BeforeEach
    public void setUp() throws ExceptionList {
        list1 = new EnrollmentList("list1", new Student("88888", "Mehrnaz"));
        list2 = new EnrollmentList("list2", new Student("77777", "Sara"));
        std = new Student("00000", "gina");
        S1 = new Section(new Course("1111111", "C1", 3), "01");
        S2 = new Section(new Course("2222222", "C2", 3), "02");
        S3 = new Section(new Course("3333333", "C3", 3), "01");
        S4 = new Section(new Course("3333333", "C3", 3), "01");
        this.lists = List.of(this.list1, this.list2);

        given(enrollmentListRepository.findAll()).willReturn(this.lists);
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        given(studentRepository.findByStudentNumber(new StudentNumber("00000"))).willReturn(Optional.of(std));
    }

    @Test
    public void All_lists_are_returned_correctly() throws Exception {
        mvc.perform(get("/lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].enrollmentListName", is("list1")))
                .andExpect(jsonPath("$[1].enrollmentListName", is("list2")));
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
        JSONObject req = new JSONObject();
        req.put("enrollmentListName", "new_list");
        mvc.perform(post("/lists/00000")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("new_list")));
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
        EnrollmentList list = new EnrollmentList("Mahsa's List", std);
        List<EnrollmentListView> lists = new ArrayList<>();
        lists.add(new EnrollmentListView(list));
        given(studentRepository.findAllListsForStudent("00000")).willReturn(lists);
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
    public void All_sections_of_list_are_returned_correctly() throws Exception {
        this.list1.addSections(S1, S2, S3);
        mvc.perform(get("/lists/12/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)));
    }

    @Test
    public void No_violations_are_returned_for_a_valid_list() throws Exception {
        Section section = new Section(new Course("4444444", "dm", 3), "01");
        this.list1.addSections(S1, S2, S3, section);
        mvc.perform(get("/lists/12/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(0)));
    }

    @Test
    public void Violations_of_unacceptable_list_are_returned_correctly() throws Exception {
        Course ap = new Course("1111111", "ap", 4);
        Course dm = new Course("2222222", "dm", 4);
        Section ap_1 = new Section(ap, "01");
        Section ap_2 = new Section(ap, "02");
        Section dm_1 = new Section(dm, "01");
        this.list1.addSections(ap_1, ap_2, dm_1);
        String error = String.format("%s is requested to be taken twice", ap);
        mvc.perform(get("/lists/12/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages[0]", is(error)));
    }

    @Test
    public void Section_is_added_correctly_to_the_requested_list() throws Exception {
        Section newSec = new Section(new Course("2222222", "dm", 3), "01");
        given(sectionRepository.findById(2L)).willReturn(Optional.of(newSec));

        mvc.perform(MockMvcRequestBuilders.put("/lists/12/sections/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void EnrollmentList_section_is_not_returned_if_enrollmentList_is_not_Found() throws Exception{
        mvc.perform(get("/lists/1/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Section_is_removed_correctly_from_the_requested_list() throws Exception {
        Section newSec = new Section(new Course("2222222", "dm", 3), "01");
        given(sectionRepository.findById(2L)).willReturn(Optional.of(newSec));

        mvc.perform(MockMvcRequestBuilders.delete("/lists/12/sections/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void Section_cannot_be_removed_from_list_if_requested_list_does_not_exist() throws Exception {
        Section section = new Section(new Course("2222222", "dm", 3), "01");
        given(sectionRepository.findById(2L)).willReturn(Optional.of(section));

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
    }

    @Test
    public void EnrollmentList_check_is_returned_correctly_when_there_is_violation() throws Exception{
        EnrollmentList list = new EnrollmentList("list", new Student("1", "Std"));
        list.addSections(S1, S2, S3, S4);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        List<String> err = new ArrayList<>();
        err.add("[3333333] C3 is requested to be taken twice");
        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages", is(err)));
    }

    @Test
    public void EnrollmentList_check_is_not_found_if_it_doesnt_exist() throws Exception{
        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void EnrollmentList_check_is_returned_correctly_with_more_than_one_violations() throws Exception{
        Student student = mock(Student.class);
        EnrollmentList list = new EnrollmentList("list", student);
        Section S5 = new Section(new Course("5555555", "dm", 3), "01");
        list.addSections(S1, S2, S3, S4, S5);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        when(student.calculateGPA()).thenReturn(new Grade(11.99));
        when(student.getTotalTakenCredits()).thenReturn(1);

        List<String> err = new ArrayList<>(List.of("[3333333] C3 is requested to be taken twice", "Maximum number of credits(14) exceeded."));
        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(2)))
                .andExpect(jsonPath("$.violationMessages", is(err)));
    }

}
