package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.major.MajorController;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.repository.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MajorController.class)
public class MajorControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private MajorRepository majorRepository;
    @MockBean
    private FacultyRepository facultyRepository;

    @Test
    public void All_majors_are_returned_correctly() throws Exception{
        Major m1 = new Major("8101", "CS");
        Major m2 = new Major("8102", "CHEM");
        Major m3 = new Major("8103", "POLY");
        given(majorRepository.findAll()).willReturn(List.of(m1, m2, m3));
        mvc.perform(get("/majors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].majorName", is("CS")))
                .andExpect(jsonPath("$[0].majorNumber", is("8101")))
                .andExpect(jsonPath("$[1].majorName", is("CHEM")))
                .andExpect(jsonPath("$[1].majorNumber", is("8102")));
    }

    @Test
    public void Major_courses_is_returned_correctly() throws Exception{
        Major major1 = new Major("8101", "CE");
        Course c1 = new Course("1111111", "C1", 3);
        Course c2 = new Course("1111112", "C2", 3);
        Course c3 = new Course("1111113", "C3", 3);
        major1.addCourse(c1, c2, c3);
        given(majorRepository.findById(1L)).willReturn(java.util.Optional.of(major1));
        mvc.perform(get("/majors/1/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].courseTitle", is("C1")))
                .andExpect(jsonPath("$[1].courseCredits", is(3)));
    }

    @Test
    public void Valid_major_is_added_correctly() throws Exception{
        Faculty faculty = mock(Faculty.class);
        JSONObject request = new JSONObject();
        request.put("majorName", "CHEM");
        request.put("majorNumber", "8102");
        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(faculty));
        mvc.perform(post("/majors/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName", is("CHEM")))
                .andExpect(jsonPath("$.majorNumber", is("8102")));
    }

    @Test
    public void Major_cannot_be_added_if_name_already_exists() throws Exception{
        Faculty faculty = mock(Faculty.class);
        Major major = mock(Major.class);
        JSONObject request = new JSONObject();
        request.put("majorName", "EE");
        request.put("majorNumber", "8101");

        JSONObject response = new JSONObject();
        response.put("1", "Major with name EE exists.");

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(faculty));
        given(majorRepository.findByMajorName("EE")).willReturn(java.util.Optional.ofNullable(major));
        mvc.perform(post("/majors/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }

    @Test
    public void Major_cannot_be_added_if_faculty_does_not_exists() throws Exception{
        Major major = mock(Major.class);
        JSONObject request = new JSONObject();
        request.put("majorName", "EE");
        request.put("majorNumber", "8101");
        given(majorRepository.findByMajorName("EE")).willReturn(java.util.Optional.ofNullable(major));
        mvc.perform(post("/majors/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "Faculty not found"));
    }

    @Test
    public void Major_name_and_number_cannot_be_empty() throws Exception{
        Faculty faculty = mock(Faculty.class);
        JSONObject request = new JSONObject();
        request.put("majorName", "");
        request.put("majorNumber", "");

        JSONObject response = new JSONObject();
        response.put("1", "Major name can not be empty");
        response.put("2", "Major number can not be empty");

        given(facultyRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(faculty));

        mvc.perform(post("/majors/1")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }
}
