package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.program.ProgramController;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgramController.class)
public class ProgramControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProgramRepository programRepository;
    @MockBean
    private MajorRepository majorRepository;

    @Test
    public void All_programs_are_returned_correctly() throws Exception{
        Major cs = new Major("8101", "CS");
        Major ee = new Major("8101", "EE");
        Program p1 = new Program(cs, "Undergraduate", 140, 140, "Major");
        Program p2 = new Program(cs, "Undergraduate", 20, 32, "Minor");
        Program p3 = new Program(ee, "Undergraduate", 20, 32, "Minor");

        given(programRepository.findAll()).willReturn(List.of(p1, p2, p3));
        mvc.perform(get("/programs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].majorName", is("CS")))
                .andExpect(jsonPath("$[0].majorNumber", is("8101")))
                .andExpect(jsonPath("$[0].graduateLevel", is("Undergraduate")))
                .andExpect(jsonPath("$[1].type", is("Minor")))
                .andExpect(jsonPath("$[1].minimum", is(20)))
                .andExpect(jsonPath("$[1].maximum", is(32)));
    }

    @Test
    public void Program_cannot_be_added_if_major_is_not_found() throws Exception {
        JSONObject request = new JSONObject();
        request.put("graduateLevel", "Undergraduate");
        request.put("type", "Major");
        request.put("minimum", 140);
        request.put("maximum", 140);

        mvc.perform(post("/programs/17")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void New_valid_program_is_added_correctly() throws Exception {
        Major cs = new Major("8101", "CS");
        JSONObject request = new JSONObject();
        request.put("graduateLevel", "Undergraduate");
        request.put("type", "Major");
        request.put("minimum", 140);
        request.put("maximum", 140);

        given(majorRepository.findById(17L)).willReturn(java.util.Optional.of(cs));
        mvc.perform(post("/programs/17")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.majorName", is("CS")))
                .andExpect(jsonPath("$.majorNumber", is("8101")))
                .andExpect(jsonPath("$.graduateLevel", is("Undergraduate")))
                .andExpect(jsonPath("$.type", is("Major")))
                .andExpect(jsonPath("$.minimum", is(140)))
                .andExpect(jsonPath("$.maximum", is(140)));
    }

    @Test
    public void Program_with_invalid_graduate_level_cannot_be_added() throws Exception {
        Major cs = new Major("8101", "CS");
        JSONObject request = new JSONObject();
        request.put("graduateLevel", "bachelors");
        request.put("type", "regular");
        request.put("minimum", 150);
        request.put("maximum", 140);

        JSONObject response = new JSONObject();
        response.put("1", "Graduate level is not valid.");
        response.put("2", "Maximum number of credits must larger than the minimum.");
        response.put("3", "Program type is not valid.");

        given(majorRepository.findById(17L)).willReturn(java.util.Optional.of(cs));
        mvc.perform(post("/programs/17")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }
}
