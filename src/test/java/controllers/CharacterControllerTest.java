package controllers;

import com.javaprojects.tvshowapi.controllers.CharacterController;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.services.CharacterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CharacterControllerTest {
    @Mock
    private CharacterService characterService;

    @InjectMocks
    private CharacterController characterController;

    private MockMvc mockMvc;

    private static final Character character = new Character();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(characterController).build();

        character.setId(1L);
        character.setName("Alex");
    }

    @Test
    void getCharactersTest() throws Exception {
        mockMvc.perform(get("/characters/get-all")).andExpect(status().isOk());
    }

    @Test
    void searchByNameTest() throws Exception {
        mockMvc.perform(get("/characters/get?name=Alex")).andExpect(status().isOk());
    }

    @Test
    void insertCharacterTest() throws Exception {
        mockMvc.perform(post("/characters/post?id=1")).andExpect(status().isOk());
    }

    @Test
    void deleteCharacterTest() throws Exception {
        mockMvc.perform(delete("/characters/delete?id=1")).andExpect(status().isOk());
    }

    @Test
    void updateCharacterTest() throws Exception {
        mockMvc.perform(put("/characters/update", character)).andExpect(status().isOk());
    }

    @Test
    void searchByTVShowTitleTest() throws Exception {
        mockMvc.perform(get("/characters/get-by-title")).andExpect(status().isOk());
    }

}
