package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Course;
import ir.proprog.enrollassist.domain.ExamTime;
import ir.proprog.enrollassist.domain.Section;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SectionController.class)
public class SectionControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private SectionRepository sectionRepository;
    @MockBean
    private EnrollmentListRepository enrollmentListRepository;
    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void All_sections_are_returned_correctly() throws Exception {
        ExamTime exam0 = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        ExamTime exam1 = new ExamTime("2021-07-11T09:00", "2021-07-11T11:00");
        List<Section> sections = List.of(
                new Section(new Course("1111111", "C1", 3), "01", exam0),
                new Section(new Course("2222222", "C2", 3), "02", exam1),
                new Section(new Course("3333333", "C3", 3), "01", exam0)
        );

        given(sectionRepository.findAll()).willReturn(sections);
        mvc.perform(get("/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sections.size())))
                .andExpect(jsonPath("$[0].courseTitle", anyOf(is("C1"), is("C2"))))
                .andExpect(jsonPath("$[0].courseNumber", anyOf(is("1111111"), is("2"))))
                .andExpect(jsonPath("$[0].sectionNo", anyOf(is("01"), is("02"))));
    }

    @Test
    public void Requested_section_is_returned_correctly() throws Exception {
        ExamTime exam = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        Section section = new Section(new Course("1111111", "ap", 3), "01", exam);
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section));
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("ap")))
                .andExpect(jsonPath("$.courseNumber", is("1111111")))
                .andExpect(jsonPath("$.sectionNo", is("01")));

    }

    @Test
    public void All_section_demands_are_returned_correctly() throws Exception {
        ExamTime exam0 = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        ExamTime exam1 = new ExamTime("2021-07-11T09:00", "2021-07-11T11:00");
        ExamTime exam2 = new ExamTime("2021-07-12T09:00", "2021-07-12T11:00");
        Section s1 = new Section(new Course("1111111", "C1", 3), "01", exam0);
        SectionView c1 = new SectionView(s1);
        SectionDemandView sd_1 = new SectionDemandView(c1, 50);
        Section s2 = new Section(new Course("2222222", "C2", 3), "01", exam1);
        SectionView c2 = new SectionView(s2);
        SectionDemandView sd_2 = new SectionDemandView(c2, 72);
        Section s3 = new Section(new Course("3333333", "C3", 3), "01", exam2);
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
    public void Section_that_doesnt_exist_is_not_found() throws Exception{
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void One_section_is_returned_correctly() throws Exception {
        ExamTime exam = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        Section section = new Section(new Course("1111111", "C1", 3), "01", exam);

        given(sectionRepository.findById(1L)).willReturn(java.util.Optional.of(section));

        mvc.perform(get("/sections/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C1")))
                .andExpect(jsonPath("$.courseNumber", is("1111111")))
                .andExpect(jsonPath("$.sectionNo", is("01")));
    }

    @Test
    public void Section_of_unreal_course_is_not_added_correctly() throws Exception {
        given(this.courseRepository.findById(1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        given(this.sectionRepository.findOneSectionOfSpecialCourse(1L, "01")).willReturn(Collections.emptyList());
        mvc.perform(MockMvcRequestBuilders.put("/sections/addSection/1/5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Section_with_invalid_section_number_is_not_added_correctly() throws Exception {
        Course course = mock(Course.class);
        given(this.courseRepository.findById(1L)).willReturn(Optional.ofNullable(course));
        given(this.sectionRepository.findOneSectionOfSpecialCourse(1L, "01")).willReturn(Collections.emptyList());
        JSONObject req = new JSONObject();
        req.put("sectionNo", "dm");
        req.put("courseId", "1");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Valid_section_is_added_correctly() throws Exception {
        Course course = new Course("1010101", "DM", 3);
        given(this.courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(this.sectionRepository.findOneSectionOfSpecialCourse(1L, "01")).willReturn(Collections.emptyList());
        JSONObject req = new JSONObject();
        req.put("sectionNo", "01");
        req.put("courseId", "1");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sectionNo", is("01")))
                .andExpect(status().isOk());
    }

    @Test
    public void Existing_section_is_not_added_correctly() throws Exception{
        ExamTime exam = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        Course course = new Course("1010101", "DM", 3);
        List<Section> findSections = List.of(new Section(course, "01", exam));
        given(this.courseRepository.findById(1L)).willReturn(Optional.of(course));
        given(this.sectionRepository.findOneSectionOfSpecialCourse(1L, "01")).willReturn(findSections);
        JSONObject req = new JSONObject();
        req.put("sectionNo", "01");
        req.put("courseId", "1");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

