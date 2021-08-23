package ir.proprog.enrollassist.controller;

import ir.proprog.enrollassist.domain.Major;
import ir.proprog.enrollassist.repository.*;
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
}
