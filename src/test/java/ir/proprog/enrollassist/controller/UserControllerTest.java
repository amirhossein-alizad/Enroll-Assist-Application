package ir.proprog.enrollassist.controller;


import ir.proprog.enrollassist.controller.user.UserController;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserRepository userRepository;
    private static User user1, user2;

    @BeforeAll
    static void setUp() {
         user1 = new User("Matt", "M1");
         user2 = new User("Pat", "P1");
    }

    @Test
    public void All_users_are_returned_correctly() throws Exception {
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        mvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Matt")))
                .andExpect(jsonPath("$[0].userId", is("M1")))
                .andExpect(jsonPath("$[1].name", is("Pat")))
                .andExpect(jsonPath("$[1].userId", is("P1")));
    }

    @Test
    public void User_with_given_id_is_returned_correctly() throws Exception {
        given(userRepository.findById(1L)).willReturn(java.util.Optional.of(user1));
        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Matt")))
                .andExpect(jsonPath("$.userId", is("M1")));
    }

    @Test
    public void User_that_does_not_exist_is_not_found() throws Exception {
        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), "User not found"));
    }

    @Test
    public void Valid_user_is_added_correctly() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "bebe");
        request.put("userId", "bebe77");
        mvc.perform(post("/users")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("bebe")))
                .andExpect(jsonPath("$.userId", is("bebe77")));
    }

    @Test
    public void User_name_cannot_be_empty() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "");
        request.put("userId", "bebe77");

        JSONObject response = new JSONObject();
        response.put("1", "Name can not be empty.");

        mvc.perform(post("/users")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }

    @Test
    public void User_id_cannot_be_empty() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "bebe");
        request.put("userId", "");

        JSONObject response = new JSONObject();
        response.put("1", "User Id can not be empty.");

        mvc.perform(post("/users")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }

    @Test
    public void New_user_id_must_be_unique() throws Exception {
        JSONObject request = new JSONObject();
        request.put("name", "bebe");
        request.put("userId", "bebe77");

        JSONObject response = new JSONObject();
        response.put("1", "User Id already exists.");

        given(userRepository.findByUserId("bebe77")).willReturn(Optional.ofNullable(user1));
        mvc.perform(post("/users")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> assertEquals(mvcResult.getResponse().getErrorMessage(), response.toString()));
    }
}
