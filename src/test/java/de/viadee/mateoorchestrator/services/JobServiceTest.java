package de.viadee.mateoorchestrator.services;

import de.viadee.mateoorchestrator.dtos.ReportDTO;
import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.enums.JobStatus;
import de.viadee.mateoorchestrator.exceptions.MateoInternalServerException;
import de.viadee.mateoorchestrator.exceptions.MateoScriptAbortException;
import de.viadee.mateoorchestrator.exceptions.NoFreeMateoException;
import de.viadee.mateoorchestrator.models.JobEntity;
import de.viadee.mateoorchestrator.repositories.JobRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * @author Marcel_Flasskamp
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
@SpringBootTest
@ActiveProfiles(profiles = "test")
class JobServiceTest {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @MockBean
    private MateoService mateoService;

    private static final String FILENAME = "src/main/resources/mateoInstances.txt";

    @BeforeEach
    public void init() throws IOException {
        jobService.clearRunningJobs();
        jobService.clearJobList();
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
    void testAddJobDTOs() throws IOException {
        Map<String, String> inputVariables = new HashMap<>();
        inputVariables.put("key", "value");
        List<String> out = new ArrayList<>();
        UUID result_uuid = jobService.addJob("SomeFile", JobPriority.DEFAULT, inputVariables, out);

        assertNotNull(result_uuid);
        assertEquals(JobStatus.QUEUED, jobService.getJob(result_uuid).get().getStatus());
    }

    @Test
    void testStartJob_Positiv() throws MateoScriptAbortException, MateoInternalServerException, NoFreeMateoException,
            IOException {
        // given
        String scriptFilename = "someScriptFileName";
        Map<String, String> inputVariables = new HashMap<>();
        inputVariables.put("someInputKey", "someInputValue");
        List<String> outputVariables = new ArrayList<>();
        UUID result_uuid = jobService.addJob(scriptFilename, JobPriority.DEFAULT, inputVariables, outputVariables);

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setFilename(scriptFilename);

        // when
        when(mateoService.startScriptWithVariables(scriptFilename, inputVariables, outputVariables))
                .thenReturn(reportDTO);
        jobService.setRunning(); //set Status Running for added Job
        jobService.startJob();

        // then
        JobEntity jobEntity = jobService.getJob(result_uuid).get();
        assertEquals(JobStatus.FINISHED, jobEntity.getStatus());
        assertEquals(reportDTO.getFilename(), jobEntity.getFilename());
    }

    @Test
    void testStartJob_SoftException()
            throws MateoScriptAbortException, MateoInternalServerException, NoFreeMateoException,
            IOException {
        // given
        String scriptFilename = "SoftException";
        Map<String, String> inputVariables = new HashMap<>();
        inputVariables.put("someInputKey", "someInputValue");
        List<String> outputVariables = new ArrayList<>();
        UUID result_uuid = jobService.addJob(scriptFilename, JobPriority.DEFAULT, inputVariables, outputVariables);

        // when
        when(mateoService.startScriptWithVariables(scriptFilename, inputVariables, outputVariables))
                .thenThrow(new NoFreeMateoException("lorem"));

        jobService.setRunning(); //set Status Running for added Job
        JobEntity jobEntity = jobService.getJob(result_uuid).get();
        assertEquals(JobStatus.RUNNING, jobEntity.getStatus());

        jobService.startJob();

        // then
        jobEntity = jobService.getJob(result_uuid).get();
        assertEquals(JobStatus.QUEUED, jobEntity.getStatus());
    }

    @Test
    void testStartJob_HardException()
            throws MateoScriptAbortException, MateoInternalServerException, NoFreeMateoException,
            IOException {
        // given
        String scriptFilename = "HardException";
        Map<String, String> inputVariables = new HashMap<>();
        inputVariables.put("someInputKey", "someInputValue");
        List<String> outputVariables = new ArrayList<>();
        UUID result_uuid = jobService.addJob(scriptFilename, JobPriority.DEFAULT, inputVariables, outputVariables);

        // when
        when(mateoService.startScriptWithVariables(scriptFilename, inputVariables, outputVariables))
                .thenThrow(new RuntimeException("Fail"));

        jobService.setRunning(); //set Status Running for added Job
        JobEntity jobEntity = jobService.getJob(result_uuid).get();
        assertEquals(JobStatus.RUNNING, jobEntity.getStatus());

        jobService.startJob();

        // then
        jobEntity = jobService.getJob(result_uuid).get();
        assertEquals(JobStatus.FAILED, jobEntity.getStatus());
    }

    @Test
    void testGetJobsWithState() throws IOException {
        // given
        String scriptFilename = "HardException";
        Map<String, String> inputVariables = new HashMap<>();
        inputVariables.put("someInputKey", "someInputValue");
        List<String> outputVariables = new ArrayList<>();
        UUID result_uuid = jobService.addJob(scriptFilename, JobPriority.DEFAULT, inputVariables, outputVariables);

        List<JobEntity> jobsWithState = jobService.getJobsWithState(JobStatus.QUEUED);

        assertEquals(1, jobsWithState.size());
        assertEquals(1, jobsWithState.stream().filter(job -> job.getId().equals(result_uuid)).count());
    }

    @Test
    void testGetRunningJob_Prio() throws IOException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();
        UUID jobOne = jobService.addJob("one", JobPriority.MEDIUM, inputVariables, outputVariables);
        UUID jobTwo = jobService.addJob("two", JobPriority.HIGH, inputVariables, outputVariables);
        UUID jobThree = jobService.addJob("three", JobPriority.LOW, inputVariables, outputVariables);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("two", prioJob.getScriptName());
        assertEquals(jobTwo, prioJob.getId());
    }

    @Test
    void testGetPrioJob_Prio_Two() throws IOException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();
        UUID jobOne = jobService.addJob("one", JobPriority.MEDIUM, inputVariables, outputVariables);
        UUID jobTwo = jobService.addJob("two", JobPriority.DEFAULT, inputVariables, outputVariables);
        UUID jobThree = jobService.addJob("three", JobPriority.HIGH, inputVariables, outputVariables);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("three", prioJob.getScriptName());
        assertEquals(jobThree, prioJob.getId());
    }

    @Test
    @Transactional
    void testGetPrioJob_Prio_Three() throws IOException {
        // given
        jobRepository.deleteAll();
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();
        UUID jobOne = jobService.addJob("one", JobPriority.URGENT, inputVariables, outputVariables);
        UUID jobTwo = jobService.addJob("two", JobPriority.DEFAULT, inputVariables, outputVariables);
        UUID jobThree = jobService.addJob("three", JobPriority.HIGH, inputVariables, outputVariables);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("one", prioJob.getScriptName());
    }

    @Test
    @Transactional
    void testGetPrioJob_AskedTimes() throws IOException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();

        UUID jobOne = jobService.addJob("one", JobPriority.DEFAULT, inputVariables, outputVariables);
        UUID jobTwo = jobService.addJob("two", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobTwo).setAskedTimes(2);
        UUID jobThree = jobService.addJob("three", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobThree).setAskedTimes(1);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("two", prioJob.getScriptName());
        assertEquals(jobTwo, prioJob.getId());
    }

    @Test
    @Transactional
    void testGetPrioJob_Date() throws IOException, ParseException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();

        UUID jobOne = jobService.addJob("one", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobOne).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020"));

        UUID jobTwo =  jobService.addJob("two", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobTwo).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("29.06.1991"));

        UUID jobThree =  jobService.addJob("three", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobThree).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("05.05.1995"));

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("two", prioJob.getScriptName());
        assertEquals(jobTwo, prioJob.getId());
    }

    @Test
    @Transactional
    void testGetPrioJob_Multi() throws ParseException, IOException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();

        UUID jobOne = jobService.addJob("one", JobPriority.URGENT, inputVariables, outputVariables);
        jobRepository.getOne(jobOne).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.2020"));
        jobRepository.getOne(jobOne).setAskedTimes(1);

        UUID jobTwo = jobService.addJob("two", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobTwo).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("29.06.1991"));

        UUID jobThree = jobService.addJob("three", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobThree).setCreateDate(new SimpleDateFormat("dd.MM.yyyy").parse("05.05.1995"));
        jobRepository.getOne(jobThree).setAskedTimes(2);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("one", prioJob.getScriptName());
        assertEquals(jobOne, prioJob.getId());
    }

    @Test
    @Transactional
    void testGetPrioJob_Multi_Two() throws IOException {
        // given
        Map<String, String> inputVariables = new HashMap<>();
        List<String> outputVariables = new ArrayList<>();

        UUID jobOne = jobService.addJob("one", JobPriority.URGENT, inputVariables, outputVariables);
        jobRepository.getOne(jobOne).setAskedTimes(2);

        UUID jobTwo = jobService.addJob("two", JobPriority.URGENT, inputVariables, outputVariables);

        UUID jobThree = jobService.addJob("three", JobPriority.DEFAULT, inputVariables, outputVariables);
        jobRepository.getOne(jobThree).setAskedTimes(4);

        jobService.setRunning();
        JobEntity prioJob = jobService.getRunningJob();

        assertEquals("one", prioJob.getScriptName());
        assertEquals(jobOne, prioJob.getId());
    }

}