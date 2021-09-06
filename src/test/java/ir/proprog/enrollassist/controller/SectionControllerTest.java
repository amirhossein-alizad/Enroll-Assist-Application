package ir.proprog.enrollassist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.controller.section.SectionController;
import ir.proprog.enrollassist.controller.section.SectionDemandView;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.EnrollmentListRepository;
import ir.proprog.enrollassist.repository.SectionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ExamTime exam1, exam2;
    private Section section;
    private Course course;

    @BeforeEach
    public void setUp() throws Exception {
        exam1 = new ExamTime("2021-07-10T09:00", "2021-07-10T11:00");
        exam2 = new ExamTime("2021-08-10T09:00", "2021-08-10T11:00");
        course = new Course("1111111", "C1", 3, "Undergraduate");
        section = new Section(course, "01", exam1, Collections.emptySet());
        given(sectionRepository.findById(1L)).willReturn(Optional.of(section));
        given(courseRepository.findById(2L)).willReturn(Optional.ofNullable(course));
    }

    @Test
    public void All_sections_are_returned_correctly() throws Exception {
        given(sectionRepository.findAll()).willReturn(List.of(section));
        mvc.perform(get("/sections")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseTitle", is(section.getCourse().getTitle())))
                .andExpect(jsonPath("$[0].courseNumber.courseNumber",  is(section.getCourse().getCourseNumber().getCourseNumber())))
                .andExpect(jsonPath("$[0].sectionNo", is(section.getSectionNo())));
    }

    @Test
    public void Requested_section_is_returned_correctly() throws Exception {
        mvc.perform(get("/sections/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is(section.getCourse().getTitle())))
                .andExpect(jsonPath("$.courseNumber.courseNumber", is(section.getCourse().getCourseNumber().getCourseNumber())))
                .andExpect(jsonPath("$.sectionNo", is(section.getSectionNo())));
    }

    @Test
    public void All_section_demands_are_returned_correctly() throws Exception {
        SectionDemandView sd_1 = new SectionDemandView(new SectionView(section), 50);
        Section otherSection = new Section(course, "02", exam2, Collections.emptySet());
        SectionDemandView sd_2 = new SectionDemandView(new SectionView(otherSection), 72);

        given(enrollmentListRepository.findDemandForAllSections()).willReturn(List.of(sd_1, sd_2));
        given(sectionRepository.findById(sd_1.getSectionId())).willReturn(java.util.Optional.of(section));
        given(sectionRepository.findById(sd_2.getSectionId())).willReturn(java.util.Optional.of(otherSection));

        mvc.perform(get("/sections/demands")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].demand", is(50)))
                .andExpect(jsonPath("$[1].demand", is(72)));
    }

    @Test
    public void Section_that_doesnt_exist_is_not_found() throws Exception{
        mvc.perform(get("/sections/47")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Section_is_removed_from_lists_correctly() throws Exception {
        EnrollmentList list1 = new EnrollmentList("SampleList1", mock(Student.class));
        EnrollmentList list2 = new EnrollmentList("SampleList2", mock(Student.class));
        list1.addSections(section);
        list2.addSections(section);

        mvc.perform(delete("/sections/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseTitle", is("C1")))
                .andExpect(jsonPath("$.courseNumber.courseNumber", is("1111111")))
                .andExpect(jsonPath("$.sectionNo", is("01")))
                .andReturn();

        assertThat(enrollmentListRepository.findEnrollmentListContainingSection(1L)).isEmpty();
        verify(sectionRepository, times(1)).delete(section);
    }

    @Test
    public void Section_cannot_be_removed_if_id_is_not_found() throws Exception {
        mvc.perform(delete("/sections/6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Section_with_invalid_section_number_is_not_added_correctly() throws Exception {
        given(this.sectionRepository.findOneSectionOfSpecialCourse(2L, "dm")).willReturn(Collections.emptyList());
        JSONObject req = new JSONObject();
        req.put("sectionNo", "dm");
        req.put("courseId", "2");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void Valid_section_is_added_correctly() throws Exception {
        given(this.sectionRepository.findOneSectionOfSpecialCourse(2L, "01")).willReturn(Collections.emptyList());
        JSONObject req = new JSONObject();
        req.put("sectionNo", "01");
        req.put("courseId", "2");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sectionNo", is("01")))
                .andExpect(status().isOk());
        verify(sectionRepository, times(1)).save(section);
    }

    @Test
    public void Existing_section_is_not_added_correctly() throws Exception{
        given(this.sectionRepository.findOneSectionOfSpecialCourse(2L, "01")).willReturn(List.of(section));
        JSONObject req = new JSONObject();
        req.put("sectionNo", "01");
        req.put("courseId", "2");
        mvc.perform(MockMvcRequestBuilders.post("/sections")
                .content(req.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ExamTime_can_be_changed_by_valid_another_examTime() throws Exception {
        JSONObject jsonExam = new JSONObject();
        jsonExam.put("start", "2020-08-10T09:00");
        jsonExam.put("end", "2020-08-10T11:00");
        mvc.perform(MockMvcRequestBuilders.put("/sections/1/setExamTime")
                .content(jsonExam.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(sectionRepository, times(1)).save(section);
    }

    @Test
    public void ExamTime_cannot_be_changed_if_section_does_not_exist() throws Exception {
        JSONObject jsonExam = new JSONObject();
        jsonExam.put("start", "2020-08-10T09:00");
        jsonExam.put("end", "2020-08-10T11:00");
        mvc.perform(MockMvcRequestBuilders.put("/sections/47/setExamTime")
                .content(jsonExam.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Schedule_is_not_set_if_section_is_not_found() throws Exception {
        mvc.perform(put("/sections/15/setSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                        .content("[\n{\n\"dayOfWeek\": \"Sunday\",\n\"endTime\": \"10:00\",\n\"startTime\": \"12:00\"\n}\n]"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void Number_of_new_conflicts_that_happen_return_correctly() throws Exception {
        EnrollmentList enrollmentList1 = mock(EnrollmentList.class);
        EnrollmentList enrollmentList2 = mock(EnrollmentList.class);
        given(enrollmentListRepository.findEnrollmentListContainingSection(1L)).willReturn(List.of(enrollmentList1, enrollmentList2));
        when(enrollmentList1.makeExamTimeConflict(section, exam2)).thenReturn(false);
        when(enrollmentList2.makeExamTimeConflict(section, exam2)).thenReturn(true);
        JSONObject jsonExam = new JSONObject();
        jsonExam.put("start", "2021-08-10T09:00");
        jsonExam.put("end", "2021-08-10T11:00");
        mvc.perform(MockMvcRequestBuilders.put("/sections/1/getExamTimeConflicts")
                .content(jsonExam.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfConflict", is(1)));
    }


    @Test
    public void New_conflict_happens_if_new_schedule_time_has_conflict_with_other() throws Exception {
        PresentationSchedule schedule1 = new PresentationSchedule("Monday", "09:00", "11:00");
        EnrollmentList enrollmentList = mock(EnrollmentList.class);
        given(this.enrollmentListRepository.findEnrollmentListContainingSection(1L)).willReturn(List.of(enrollmentList));
        when(enrollmentList.makePresentationScheduleConflict(section, List.of(schedule1))).thenReturn(true);
        ObjectMapper Obj = new ObjectMapper();
        mvc.perform(MockMvcRequestBuilders.put("/sections/1/getScheduleConflicts")
                .content(Obj.writeValueAsString(List.of(schedule1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfConflict", is(1)));

    }
}

