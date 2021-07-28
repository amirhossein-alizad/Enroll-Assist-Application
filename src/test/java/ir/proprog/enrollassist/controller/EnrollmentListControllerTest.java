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


import static org.mockito.Mockito.mock;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
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
    public void One_list_is_returned_correctly() throws Exception {
        Student bob  = mock(Student.class);
        EnrollmentList list = new EnrollmentList("bob's list", bob);

        given(enrollmentListRepository.findById(1L)).willReturn(java.util.Optional.of(list));

        mvc.perform(get("/lists/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentListName", is("bob's list")));
    }

}
