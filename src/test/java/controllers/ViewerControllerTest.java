package controllers;

import com.javaprojects.tvshowapi.controllers.ViewerController;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.services.ViewerService;
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
class ViewerControllerTest {
    @Mock
    private ViewerService viewerService;

    @InjectMocks
    private ViewerController viewerController;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(viewerController).build();

    }

    @Test
    void getViewersTest() throws Exception {
        mockMvc.perform(get("/viewers/get-all")).andExpect(status().isOk());
    }

    @Test
    void searchByNameTest() throws Exception {
        mockMvc.perform(get("/viewers/get")).andExpect(status().isOk());
    }

    @Test
    void insertViewerTest() throws Exception {
        mockMvc.perform(post("/viewers/post")).andExpect(status().isOk());
    }

    @Test
    void deleteViewerTest() throws Exception {
        mockMvc.perform(delete("/viewers/delete")).andExpect(status().isOk());
    }

    @Test
    void updateViewerTest() throws Exception {
        mockMvc.perform(put("/viewers/update")).andExpect(status().isOk());
    }

    @Test
    void getWatchedTVShowsTest() throws Exception {
        mockMvc.perform(get("/viewers/get-watched")).andExpect(status().isOk());
    }

    @Test
    void addToWatchedTest() throws Exception {
        mockMvc.perform(put("/viewers/add-to-watched")).andExpect(status().isOk());
    }
}
