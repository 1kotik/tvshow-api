package controllers;

import com.javaprojects.tvshowapi.controllers.TVShowController;
import com.javaprojects.tvshowapi.services.TVShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TVShowControllerTest {
    @Mock
    private TVShowService tvShowService;

    @InjectMocks
    private TVShowController tvShowController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tvShowController).build();
    }

    @Test
    void getTVShowsTest() throws Exception{
        mockMvc.perform(get("/tvshows/get-all")).andExpect(status().isOk());
    }

    @Test
    void searchByTitleTest() throws Exception{
        mockMvc.perform(get("/tvshows/get")).andExpect(status().isOk());
    }

    @Test
    void insertTVShowTest() throws Exception{
        mockMvc.perform(post("/tvshows/post")).andExpect(status().isOk());
    }

    @Test
    void deleteTVShowTest() throws Exception{
        mockMvc.perform(delete("/tvshows/delete")).andExpect(status().isOk());
    }

    @Test
    void updateTVShowTest() throws Exception{
        mockMvc.perform(put("/tvshows/update")).andExpect(status().isOk());
    }

    @Test
    void getCharactersTest() throws Exception{
        mockMvc.perform(get("/tvshows/get-characters")).andExpect(status().isOk());
    }
}
