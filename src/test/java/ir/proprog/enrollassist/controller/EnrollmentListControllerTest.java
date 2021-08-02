package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
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
    EnrollmentList list1;
    EnrollmentList list2;

    @BeforeEach
    public void setUp() {
        this.list1 = new EnrollmentList("list1", new Student("88888", "Mehrnaz"));
        this.list2 = new EnrollmentList("list1", new Student("77777", "Sara"));
        this.lists = List.of(this.list1, this.list2);
    }

    @Test
    public void All_lists_are_returned_correctly() throws Exception {
        given(enrollmentListRepository.findAll()).willReturn(this.lists);
        mvc.perform(get("/lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(this.lists.size())))
                .andExpect(jsonPath("$[0].enrollmentListName", is(this.lists.get(0).getListName())))
                .andExpect(jsonPath("$[1].enrollmentListName", is(this.lists.get(1).getListName())));
    }

    @Test
    public void Requested_list_is_returned_correctly() throws Exception {
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is(this.list1.getListName())));
    }

    @Test
    public void New_list_is_added_correctly() throws Exception {
        Student std = new Student("00000", "gina");
        JSONObject req = new JSONObject();
        req.put("studentNumber", "00000");
        req.put("listName", "new_list");
        given(studentRepository.findStudentByStudentNumber("00000")).willReturn(std);
        mvc.perform(post("/lists")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("new_list")));
    }

    @Test
    public void New_list_cannot_be_added_if_owners_student_number_does_not_exist() throws Exception {
        JSONObject req = new JSONObject();
        req.put("studentNumber", "00000");
        req.put("listName", "new_list");
        mvc.perform(post("/lists")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void List_that_doesnt_exist_is_not_found() throws Exception {
        given(enrollmentListRepository.findById(12L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        mvc.perform(get("/lists/12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void List_with_no_sections_is_returned_correctly() throws Exception {
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void All_sections_of_list_are_returned_correctly() throws Exception {
        this.list1.addSection(new Section(new Course("1", "ap", 3), "01"));
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].courseTitle", is("ap")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)));
    }

    @Test
    public void No_violations_are_returned_for_a_valid_list() throws Exception {
        this.list1.addSection(new Section(new Course("1", "ap", 3), "01"));
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(0)));
    }

    @Test
    public void Violations_of_unacceptable_list_are_returned_correctly() throws Exception {
        Course ap = new Course("1", "ap", 3);
        this.list1.addSection(new Section(ap, "01"));
        this.list1.addSection(new Section(ap, "02"));
        String error = String.format("%s is requested to be taken twice", ap);
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages[0]", is(error)));
    }

    @Test
    public void Section_is_Added_correctly_to_the_requested_list() throws Exception {
        Section newSec = new Section(new Course("2", "dm", 3), "01");
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
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
    public void EnrollmentList_check_is_returned_correctly_when_there_is_violation() throws Exception{
        EnrollmentList list = new EnrollmentList("list", new Student("1", "Std"));

        Section S1 = new Section(new Course("1", "C1", 3), "01");
        Section S2 = new Section(new Course("2", "C2", 3), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        Section S4 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3, S4);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        List<String> err = new ArrayList<>();
        err.add("[3] C3 is requested to be taken twice");
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
        Student std = mock(Student.class);

        EnrollmentList list = new EnrollmentList("list", std);

        Section S1 = new Section(new Course("1", "C1", 5), "01");
        Section S2 = new Section(new Course("2", "C2", 5), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        Section S4 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3, S4);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        when(std.calculateGPA()).thenReturn(11.99F);
        when(std.getTotalTakenCredits()).thenReturn(1);

        List<String> err = new ArrayList<>(List.of("[3] C3 is requested to be taken twice", "Maximum number of credits(14) exceeded."));
        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(2)))
                .andExpect(jsonPath("$.violationMessages", is(err)));
    }

}
