package de.viadee.mateoorchestrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.viadee.mateoorchestrator.MateoInstances;
import de.viadee.mateoorchestrator.dtos.ReportDTO;
import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.enums.JobStatus;
import de.viadee.mateoorchestrator.exceptions.MateoInternalServerException;
import de.viadee.mateoorchestrator.exceptions.MateoScriptAbortException;
import de.viadee.mateoorchestrator.exceptions.NoFreeMateoException;
import de.viadee.mateoorchestrator.models.JobEntity;
import de.viadee.mateoorchestrator.repositories.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * @author Marcel_Flasskamp
 */
@Service
public class JobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    public static final String FILE_JOB_LIST = "src/main/resources/jobList";

    private final JobRepository jobRepository;

    private final MateoService mateoService;

    /**
     * The key represents the uuid of the job <br>
     * The value represents the job itself
     */
    protected final Set<UUID> runningJobs = new HashSet<>();

    public JobService(MateoService mateoService, JobRepository jobRepository) {
        this.mateoService = mateoService;
        this.jobRepository = jobRepository;
    }

    /**
     * generate a new Job with uuid, scriptFile, inputVariables and status queued
     * add generated Job to JobList
     *
     * @param scriptFile     path to mateoScriptFile
     * @param inputVariables input/output variables
     * @return uuid of generated Job
     */
    public UUID addJob(String scriptFile, JobPriority jobPriority, Map<String, String> inputVariables,
            List<String> outputVariables) {
        JobEntity jobEntity = new JobEntity(scriptFile, jobPriority, inputVariables, outputVariables);
        jobRepository.saveAndFlush(jobEntity);
        return jobEntity.getId();
    }

    /**
     * asynchronous method to start jobs from jobList which are in 'queued' status
     */
    @Async
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void startJob() {
        JobEntity jobEntity = getRunningJob();
        if (Objects.nonNull(jobEntity)) {
            try {
                jobEntity.setAskedTimes(jobEntity.getAskedTimes() + 1);
                ReportDTO reportDTO = mateoService
                        .startScriptWithVariables(jobEntity.getScriptName(), jobEntity.getInputVariables(),
                                new ArrayList<>(jobEntity.getOutputVariables().keySet()));
                mapReportDtoToEntity(reportDTO, jobEntity);
                jobEntity.setStatus(JobStatus.FINISHED);
                runningJobs.remove(jobEntity.getId());
            } catch (NoFreeMateoException | JsonProcessingException | MateoScriptAbortException | MateoInternalServerException e) {
                LOGGER.warn(e.getMessage(), e);
                jobEntity.setStatus(JobStatus.QUEUED);
                runningJobs.remove(jobEntity.getId());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                jobEntity.setStatus(JobStatus.FAILED);
                runningJobs.remove(jobEntity.getId());
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void setRunning() {
        if (runningJobs.size() < MateoInstances.getMateos().size()) {
            JobEntity job = jobRepository.findQueuedJobSorted();
            if (Objects.nonNull(job))
                job.setStatus(JobStatus.RUNNING);
        }
    }

    public JobEntity getRunningJob() {
        Optional<JobEntity> jobEntity = jobRepository.findAllRunningJobsSorted().stream()
                .filter(job -> !runningJobs.contains(job.getId())).findFirst();

        if (jobEntity.isPresent()) {
            runningJobs.add(jobEntity.get().getId());
            return jobEntity.get();
        }
        return null;
    }

    protected void mapReportDtoToEntity(ReportDTO reportDTO, JobEntity jobEntity) {
        jobEntity.setFilename(reportDTO.getFilename());
        jobEntity.setOriginFileName(reportDTO.getOriginFileName());
        jobEntity.setTestSetName(reportDTO.getTestSetName());
        jobEntity.setResultLevel(reportDTO.getResultLevel());
        jobEntity.setResultString(reportDTO.getResultString());
        jobEntity.setStartTime(reportDTO.getStartTime());
        jobEntity.setOriginFileName(reportDTO.getOriginFileName());
        jobEntity.setRunIndex(reportDTO.getRunIndex());
        jobEntity.setVtfVersion(reportDTO.getVtfVersion());
        jobEntity.setMateoInstanz(reportDTO.getMateoInstanz());
        jobEntity.setOutputVariables(reportDTO.getOutputVariables());
        jobEntity.setFilePath(reportDTO.getFilePath());
    }

    /**
     * return the job of given uuid
     *
     * @param uuid uuid of the job to return
     * @return job with given uuid
     */
    public Optional<JobEntity> getJob(UUID uuid) {
        return jobRepository.findById(uuid);
    }

    /**
     * return the jobs with given state
     *
     * @param state state of job to return
     * @return job with given state
     */
    public List<JobEntity> getJobsWithState(JobStatus state) {
        return jobRepository.findAllByStatus(state);
    }

    /**
     * Method to get all Jobs
     *
     * @return all Jobs
     */
    public List<JobEntity> getJobs() {
        return jobRepository.findAll();
    }

    public void removeJob(UUID uuid) {
        jobRepository.deleteById(uuid);
    }

    public void clearJobList() {
        jobRepository.deleteAll();
    }

    protected void clearRunningJobs() {
        runningJobs.clear();
    }

}
