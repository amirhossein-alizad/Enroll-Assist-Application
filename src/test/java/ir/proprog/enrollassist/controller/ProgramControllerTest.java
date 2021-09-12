package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.controller.major.MajorController;
import ir.proprog.enrollassist.controller.program.ProgramController;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.repository.FacultyRepository;
import ir.proprog.enrollassist.repository.MajorRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
