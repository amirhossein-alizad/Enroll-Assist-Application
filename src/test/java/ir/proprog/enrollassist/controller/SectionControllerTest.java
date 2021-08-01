package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.domain.Student;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SectionController.class)
public class SectionControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;

    @Test
    public void All_sections_are_returned_correctly() throws Exception {
        List<Section> sections = List.of(
                new Section(new Course("1", "C1", 3), "01"),
                new Section(new Course("2", "C2", 3), "02"),
                new Section(new Course("2", "C2", 3), "01")
        );

        given(sectionRepository.findAll()).willReturn(sections);

        // TODO: Since there is no requirement on the order of the returned list, $[0] must be changed to proper filtering
        mvc.perform(get("/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sections.size())))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[0].courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionNo", is("01")));
    }

    @Test
    public void Section_is_returned_correctly() throws Exception {
        Section section = new Section(new Course("1", "ap", 3), "01");
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section));
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("ap")))
                .andExpect(jsonPath("$.courseNumber", is("1")))
                .andExpect(jsonPath("$.sectionNo", is("01")));

    }

    @Test
    public void Unreal_section_not_found() throws Exception {
        given(sectionRepository.findById(1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void Demands_are_returned_correctly() throws Exception {
        SectionDemandView sectionDemandView1 = new SectionDemandView(1L, 4L);
        SectionDemandView sectionDemandView2 = new SectionDemandView(2L, 8L);
        Section section1 = new Section(new Course("1", "ap", 3), "01");
        Section section2 = new Section(new Course("2", "dm", 3), "01");
        given(enrollmentListRepository.findDemandForAllSections()).willReturn(List.of(sectionDemandView1, sectionDemandView2));
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section1));
        given(sectionRepository.findById(2L)).willReturn(Optional.of(section2));
        mvc.perform(get("/sections/demands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].demand", is(4)))
                .andExpect(jsonPath("$[0].sectionView.courseTitle", is("ap")))
                .andExpect(jsonPath("$[0].sectionView.courseNumber", is("1")))
                .andExpect(jsonPath("$[0].sectionView.sectionNo", is("01")))
                .andExpect(jsonPath("$[1].demand", is(8)))
                .andExpect(jsonPath("$[1].sectionView.courseTitle", is("dm")))
                .andExpect(jsonPath("$[1].sectionView.courseNumber", is("2")))
                .andExpect(jsonPath("$[1].sectionView.sectionNo", is("01")));
    }

    @Test
    public void All_section_demands_are_returned_correctly() throws Exception {
        Section s1 = new Section(new Course("1", "C1", 3), "01");
        SectionView c1 = new SectionView(s1);
        SectionDemandView sd_1 = new SectionDemandView(c1, 50);
        Section s2 = new Section(new Course("2", "C2", 3), "01");
        SectionView c2 = new SectionView(s2);
        SectionDemandView sd_2 = new SectionDemandView(c2, 72);
        Section s3 = new Section(new Course("3", "C3", 3), "01");
        SectionView c3 = new SectionView(s3);
        SectionDemandView sd_3 = new SectionDemandView(c3, 25);

        List<SectionDemandView> demands = List.of(sd_1, sd_2, sd_3);

        given(enrollmentListRepository.findDemandForAllSections()).willReturn(demands);

        given(sectionRepository.findById(sd_1.getSectionId())).willReturn(java.util.Optional.of(s1));
        given(sectionRepository.findById(sd_2.getSectionId())).willReturn(java.util.Optional.of(s2));
        given(sectionRepository.findById(sd_3.getSectionId())).willReturn(java.util.Optional.of(s3));

        mvc.perform(get("/sections/demands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(demands.size())))
                .andExpect(jsonPath("$[0].demand", is(50)))
                .andExpect(jsonPath("$[1].demand", is(72)))
                .andExpect(jsonPath("$[2].demand", is(25)));
    }

    @Test
    public void One_section_is_returned_correctly() throws Exception {
        Section section = new Section(new Course("1", "C1", 3), "01");

        given(sectionRepository.findById(1L)).willReturn(java.util.Optional.of(section));

        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C1")))
                .andExpect(jsonPath("$.courseNumber", is("1")))
                .andExpect(jsonPath("$.sectionNo", is("01")));
    }

    @Test
    public void Sections_that_dont_exist_are_not_found() throws Exception{
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void All_demands_are_returned_correctly() throws Exception {
        Section S1 = new Section(new Course("1", "C1", 3), "01");
        Section S2 = new Section(new Course("2", "C2", 3), "02");
        Section S3 = new Section(new Course("2", "C2", 3), "01");
        SectionView SV1 = new SectionView(S1);
        SectionView SV2 = new SectionView(S2);
        SectionView SV3 = new SectionView(S3);
        List<SectionDemandView> demandViews = List.of(new SectionDemandView(SV1, 10),
                new SectionDemandView(SV2, 20),
                new SectionDemandView(SV3, 30));

        given(enrollmentListRepository.findDemandForAllSections()).willReturn(demandViews);
        given(sectionRepository.findById(S1.getId())).willReturn(java.util.Optional.of(S1));
        given(sectionRepository.findById(S2.getId())).willReturn(java.util.Optional.of(S2));
        given(sectionRepository.findById(S3.getId())).willReturn(java.util.Optional.of(S3));

        mvc.perform(get("/sections/demands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(demandViews.size())))
                .andExpect(jsonPath("$[0].demand", is(10)))
                .andExpect(jsonPath("$[1].demand", is(20)))
                .andExpect(jsonPath("$[2].demand", is(30)));
    }
}
