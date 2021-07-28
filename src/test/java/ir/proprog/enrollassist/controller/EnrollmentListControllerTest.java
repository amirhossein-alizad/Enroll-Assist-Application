package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.EnrollmentList;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
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

    @Test
    public void All_lists_are_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        Student ben  = mock(Student.class);
        Student pete  = mock(Student.class);
        List<EnrollmentList> lists = List.of(
                new EnrollmentList("list1", bob),
                new EnrollmentList("list2", ben),
                new EnrollmentList("list3", pete),
                new EnrollmentList("list4", bob)
        );

        given(enrollmentListRepository.findAll()).willReturn(lists);

        mvc.perform(get("/lists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(lists.size())))
                .andExpect(jsonPath("$[0].enrollmentListName", is("list1")))
                .andExpect(jsonPath("$[1].enrollmentListName", is("list2")))
                .andExpect(jsonPath("$[3].enrollmentListName", is("list4")));
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
