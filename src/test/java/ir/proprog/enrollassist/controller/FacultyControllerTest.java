package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.faculty.FacultyController;
import ir.proprog.enrollassist.domain.faculty.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private MajorRepository majorRepository;
    @MockBean
    private FacultyRepository facultyRepository;

    @Test
    public void All_faculties_are_returned_correctly() throws Exception{
        Faculty f1 = new Faculty("ECE");
        Faculty f2 = new Faculty("ART");
        Faculty f3 = new Faculty("MED");
        given(facultyRepository.findAll()).willReturn(List.of(f1, f2, f3));
        mvc.perform(get("/faculties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].facultyName", is("ECE")))
                .andExpect(jsonPath("$[1].facultyName", is("ART")))
                .andExpect(jsonPath("$[2].facultyName", is("MED")));
    }

    @Test
    public void Valid_faculty_is_added_correctly() throws Exception{
        mvc.perform(post("/faculties")
                .content("{\n" +
                        "  \"facultyId\": 0,\n" +
                        "  \"facultyName\": \"ECE\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facultyName", is("ECE")));
    }

    @Test
    public void Faculty_with_empty_names_cannot_be_added() throws Exception{
        JSONObject response = new JSONObject();
        response.put("1", "Faculty name can not be Empty.");
        mvc.perform(post("/faculties")
                .content("{\n" +
                        "  \"facultyId\": 0,\n" +
                        "  \"facultyName\": \"\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }

    @Test
    public void Faculty_with_the_same_name_cannot_be_added() throws Exception{
        Faculty faculty = mock(Faculty.class);

        JSONObject response = new JSONObject();
        response.put("1", "Faculty with name ECE exists.");

        given(facultyRepository.findByFacultyName("ECE")).willReturn(java.util.Optional.ofNullable(faculty));

        mvc.perform(post("/faculties")
                .content("{\n" +
                        "  \"facultyId\": 0,\n" +
                        "  \"facultyName\": \"ECE\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }
}
