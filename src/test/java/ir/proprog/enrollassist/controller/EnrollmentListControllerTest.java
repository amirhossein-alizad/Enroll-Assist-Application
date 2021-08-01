package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    public void List_is_returned_correctly() throws Exception {
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is(this.list1.getListName())));
    }

    @Test
    public void Unreal_list_not_found() throws Exception {
        given(enrollmentListRepository.findById(12L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment List not found"));
        mvc.perform(get("/lists/12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void List_with_no_section_is_returned_correctly() throws Exception {
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
    public void Valid_list_is_accepted() throws Exception {
        this.list1.addSection(new Section(new Course("1", "ap", 3), "01"));
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        mvc.perform(get("/lists/12/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(0)));
    }

    @Test
    public void Invalid_list_isnt_accepted() throws Exception {
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
    public void Section_is_Added_correctly() throws Exception {
        Section newSec = new Section(new Course("2", "dm", 3), "01");
        given(enrollmentListRepository.findById(12L)).willReturn(Optional.of(this.list1));
        given(sectionRepository.findById(2L)).willReturn(Optional.of(newSec));

        mvc.perform(MockMvcRequestBuilders.put("/lists/12/sections/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void All_enrollmentLists_are_returned_correctly() throws Exception{
        Student A = new Student("111", "A");
        Student B = new Student("112", "B");
        List<EnrollmentList> lists = List.of(new EnrollmentList("List1", A)
                , new EnrollmentList("List2", B));

        given(enrollmentListRepository.findAll()).willReturn(lists);

        mvc.perform(get("/lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lists.size())))
                .andExpect(jsonPath("$[0].enrollmentListName", anyOf(is("List1"), is("List2"))))
                .andExpect(jsonPath("$[1].enrollmentListName", anyOf(is("List1"), is("List2"))));
    }

    @Test
    public void One_enrollmentList_is_returned_correctly() throws Exception{
        Student A = new Student("111", "A");
        EnrollmentList listA = new EnrollmentList("List1", A);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(listA));

        mvc.perform(get("/lists/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("List1")));
    }

    @Test
    public void EnrollmentLists_that_dont_exist_are_not_found() throws Exception{
        mvc.perform(get("/lists/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void EnrollmentList_section_is_returned_correctly() throws Exception{
        EnrollmentList list = new EnrollmentList("bob's list", new Student("1", "Std"));

        Section S1 = new Section(new Course("1", "C1", 3), "01");
        Section S2 = new Section(new Course("2", "C2", 3), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")))
                .andExpect(jsonPath("$[0].courseCredits", is(3)))
                .andExpect(jsonPath("$[1].courseTitle", is("C2")))
                .andExpect(jsonPath("$[1].courseNumber", is("2")))
                .andExpect(jsonPath("$[1].sectionNo", is("02")))
                .andExpect(jsonPath("$[1].courseCredits", is(3)))
                .andExpect(jsonPath("$[2].courseTitle", is("C3")))
                .andExpect(jsonPath("$[2].courseNumber", is("3")))
                .andExpect(jsonPath("$[2].sectionNo", is("01")))
                .andExpect(jsonPath("$[2].courseCredits", is(3)));
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

    @Test
    public void EnrollmentList_check_is_returned_correctly_with_no_violations() throws Exception{
        Student std = mock(Student.class);

        EnrollmentList list = new EnrollmentList("list", std);

        Section S1 = new Section(new Course("1", "C1", 5), "01");
        Section S2 = new Section(new Course("2", "C2", 5), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(0)));
    }

    @Test
    public void section_is_not_returned_if_section_is_not_found() throws Exception{
        Student std = mock(Student.class);

        EnrollmentList list = new EnrollmentList("list", std);

        Section S1 = new Section(new Course("1", "C1", 5), "01");
        Section S2 = new Section(new Course("2", "C2", 5), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(put("/lists/1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    //WHY???
    @Test
    public void section_is_returned_correctly_if_list_and_section_exist() throws Exception{
        Student std = mock(Student.class);

        EnrollmentList list = new EnrollmentList("list", std);

        Section S1 = new Section(new Course("1", "C1", 5), "01");
        Section S2 = new Section(new Course("2", "C2", 5), "02");
        Section S3 = new Section(new Course("3", "C3", 3), "01");
        list.addSections(S1, S2, S3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));
        given(sectionRepository.findById(1L)).willReturn(java.util.Optional.of(S1));

        mvc.perform(put("/lists/1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void Each_list_is_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("bob's list")));
    }

    @Test
    public void Non_existent_lists_are_not_returned() throws Exception {
        mvc.perform(get("/lists/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Each_lists_sections_are_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        Section s1 = new Section(new Course("1", "C1", 3), "01");
        Section s2 = new Section(new Course("2", "C2", 3), "02");
        Section s3 = new Section(new Course("3", "C3", 3), "01");

        list.addSections(s1, s2, s3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")));
    }

    @Test
    public void Each_list_individual_sections_are_added_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        Section s1 = new Section(new Course("1", "C1", 3), "01");
        Section s2 = new Section(new Course("2", "C2", 3), "02");
        Section s3 = new Section(new Course("3", "C3", 3), "01");

        list.addSections(s1, s2, s3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        given(sectionRepository.findById(1L)).willReturn(java.util.Optional.of(s1));

        mvc.perform(put("/lists/1/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")));
    }

    @Test
    public void List_regulations_are_checked_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        Section s1 = new Section(new Course("1", "C1", 3), "01");
        Section s2 = new Section(new Course("2", "C2", 3), "02");
        Section s3 = new Section(new Course("3", "C3", 3), "01");

        list.addSections(s1, s2, s3);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages").isEmpty());
    }

    @Test
    public void Non_existent_lists_cannot_be_checked() throws Exception {
        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void List_duplicate_courses_violation_is_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        Section s1 = new Section(new Course("1", "C1", 3), "01");
        Section s2 = new Section(new Course("2", "C2", 3), "02");

        list.addSections(s1, s2, s1);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages[0]",is("[1] C1 is requested to be taken twice")));
    }

    @Test
    public void List_maximum_allowed_credits_violation_is_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        Section s1 = new Section(new Course("1", "C1", 3), "01");
        Section s2 = new Section(new Course("2", "C2", 4), "02");
        Section s3 = new Section(new Course("3", "C3", 4), "01");
        Section s4 = new Section(new Course("4", "C4", 4), "01");

        list.addSections(s1, s2, s3, s4);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        when(bob.calculateGPA()).thenReturn(11F);

        mvc.perform(get("/lists/1/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.violationMessages", hasSize(1)))
                .andExpect(jsonPath("$.violationMessages[0]",is("Maximum number of credits is 14")));
    }

}
