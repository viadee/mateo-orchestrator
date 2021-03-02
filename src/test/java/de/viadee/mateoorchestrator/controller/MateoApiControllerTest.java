package de.viadee.mateoorchestrator.controller;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marcel_Flasskamp
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MateoApiController.class)
class MateoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String FILENAME = "src/main/resources/mateoInstances.txt";

    @BeforeEach
    public void setUp() throws IOException {
        File fileBackup = new File("src/test/resources/mateoInstancesTestdata.txt");
        File fileCopied = new File(FILENAME);

        if (fileCopied.exists())
            fileCopied.delete();
        FileUtils.copyFile(fileBackup, fileCopied);
    }

    @AfterAll
    public static void cleanUp() throws IOException {
        File fileExample = new File("src/test/resources/mateoInstancesExample.txt");
        File fileCopied = new File(FILENAME);

        if (fileCopied.exists())
            fileCopied.delete();
        FileUtils.copyFile(fileExample, fileCopied);
    }


    @Test
    void testGetMateos() throws Exception {
        mockMvc.perform(get("/api/mateo/all")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddMateoInstance_New() throws Exception {
        mockMvc.perform(post("/api/mateo/add")
                .contentType("application/json")
                .param("mateoUrl", "http://localhost:2222"))
                .andExpect(status().isCreated());
    }

    @Test
    void testAddMateoInstance_Exists() throws Exception {
        mockMvc.perform(post("/api/mateo/add")
                .contentType("application/json")
                .param("mateoUrl", "http://localhost:8123"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testAddMateoInstance_wrongUri() throws Exception {
        mockMvc.perform(post("/api/mateo/add")
                .contentType("application/json")
                .param("mateoUrl", "trash"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteMateoInstance() throws Exception {
        mockMvc.perform(delete("/api/mateo/remove")
                .contentType("application/json")
                .param("mateoUrl", "http://localhost:8123"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteMateoInstance_NotExists() throws Exception {
        mockMvc.perform(delete("/api/mateo/remove")
                .contentType("application/json")
                .param("mateoUrl", "http://localhost:3333"))
                .andExpect(status().isNotModified());
    }

    @Test
    void testDeleteMateoInstance_BadUri() throws Exception {
        mockMvc.perform(delete("/api/mateo/remove")
                .contentType("application/json")
                .param("mateoUrl", "trash"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteAllMateoInstance() throws Exception {
        mockMvc.perform(delete("/api/mateo/remove-all")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }
}