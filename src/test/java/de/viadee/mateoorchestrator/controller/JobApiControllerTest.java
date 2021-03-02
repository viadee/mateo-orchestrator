package de.viadee.mateoorchestrator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.enums.JobStatus;
import de.viadee.mateoorchestrator.models.JobEntity;
import de.viadee.mateoorchestrator.services.JobService;
import de.viadee.mateoorchestrator.services.MateoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Marcel_Flasskamp
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = JobApiController.class)
class JobApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    @MockBean
    private MateoService mateoService;

    @Test
    void testStartJob() throws Exception {
        List<String> output = new ArrayList<>();
        output.add("someOutPut");

        Map<String, String> input = new HashMap<>();
        input.put("inputKey", "inputValue");

        mockMvc.perform(post("/api/job/start")
                .contentType("application/json")
                .param("scriptFile", "test")
                .param("priority", "HIGH")
                .param("outputVariables", objectMapper.writeValueAsString(output))
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isAccepted());
    }

    @Test
    void testGetJob_FINISHED() throws Exception {
        UUID uuid = UUID.randomUUID();
        JobEntity jobEntity = new JobEntity("", JobPriority.DEFAULT, new HashMap<>(), new ArrayList<>());
        jobEntity.setStatus(JobStatus.FINISHED);
        when(jobService.getJob(uuid)).thenReturn(Optional.of(jobEntity));

        mockMvc.perform(get("/api/job")
                .contentType("application/json")
                .param("uuid", String.valueOf(uuid)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetJob_RUNNING_CheckResponseBody() throws Exception {
        UUID uuid = UUID.randomUUID();
        JobEntity jobEntity = new JobEntity("", JobPriority.DEFAULT, new HashMap<>(), new ArrayList<>());
        jobEntity.setStatus(JobStatus.RUNNING);
        when(jobService.getJob(uuid)).thenReturn(Optional.of(jobEntity));

        MvcResult mvcResult = mockMvc.perform(get("/api/job")
                .contentType("application/json")
                .param("uuid", String.valueOf(uuid)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(jobEntity), actualResponseBody);
    }

    @Test
    void testGetJob_NULL() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(jobService.getJob(uuid)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(get("/api/job")
                .contentType("application/json")
                .param("uuid", String.valueOf(uuid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetJobs() throws Exception {
        List<JobEntity> jobEntities = new ArrayList<>();
        jobEntities.add(new JobEntity("test", JobPriority.DEFAULT,new HashMap<>(), new ArrayList<>()));

        when(jobService.getJobs()).thenReturn(jobEntities);

        MvcResult mvcResult = mockMvc.perform(get("/api/job/all")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(jobEntities), actualResponseBody);
    }

    @Test
    void testGetJobsWithState() throws Exception {
        List<JobEntity> jobEntities = new ArrayList<>();
        jobEntities.add(new JobEntity("test", JobPriority.DEFAULT,new HashMap<>(), new ArrayList<>()));

        when(jobService.getJobsWithState(JobStatus.FAILED)).thenReturn(jobEntities);

        MvcResult mvcResult = mockMvc.perform(get("/api/job/state")
                .contentType("application/json")
                .param("jobState", "FAILED"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(jobEntities), actualResponseBody);
    }

    @Test
    void testGetFinishedJobs() throws Exception {
        List<JobEntity> jobEntities = new ArrayList<>();
        jobEntities.add(new JobEntity("test", JobPriority.DEFAULT,new HashMap<>(), new ArrayList<>()));

        when(jobService.getJobsWithState(JobStatus.FINISHED)).thenReturn(jobEntities);

        MvcResult mvcResult = mockMvc.perform(get("/api/job/state/finished")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(objectMapper.writeValueAsString(jobEntities), actualResponseBody);
    }

    @Test
    void testIsOnline() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/job/online")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertEquals("online", actualResponseBody);
    }

    @Test
    void testDownloadReportAsZip() throws Exception {
        UUID uuid = UUID.randomUUID();
        byte[] expectedByte = "SomeByte".getBytes();
        JobEntity jobEntity = new JobEntity("someScript", JobPriority.DEFAULT,new HashMap<>(), new ArrayList<>());

        when(jobService.getJob(uuid)).thenReturn(Optional.of(jobEntity));
        when(mateoService.downloadReport(jobEntity)).thenReturn(expectedByte);

        MvcResult mvcResult = mockMvc.perform(post("/api/job/result/download/zip")
                .contentType("application/zip")
                .param("uuid", String.valueOf(uuid)))
                .andExpect(status().isOk())
                .andReturn();

        byte[] actualResponseBody = mvcResult.getResponse().getContentAsByteArray();
        assertEquals(Arrays.toString(expectedByte), Arrays.toString(actualResponseBody));
    }

    @Test
    void testDownloadReportAsZip_WrongUuid() throws Exception {
        UUID uuid = UUID.randomUUID();

        when(jobService.getJob(uuid)).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(post("/api/job/result/download/zip")
                .contentType("application/zip")
                .param("uuid", String.valueOf(uuid)))
                .andExpect(status().isBadRequest());
    }
}