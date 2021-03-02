package de.viadee.mateoorchestrator.controller;

import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.enums.JobStatus;
import de.viadee.mateoorchestrator.models.JobEntity;
import de.viadee.mateoorchestrator.services.JobService;
import de.viadee.mateoorchestrator.services.MateoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Marcel_Flasskamp
 */
@Controller
@RequestMapping({ "/api/job" })
@Tag(name = "Job API", description = "Rest endpoint for orchestrator jobs")
public class JobApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobApiController.class);

    private final JobService jobService;

    private final MateoService mateoService;

    public JobApiController(JobService jobService, MateoService mateoService) {
        this.jobService = jobService;
        this.mateoService = mateoService;
    }

    @PostMapping(value = "/start")
    @Operation(summary = "Start job",
            responses = {
                    @ApiResponse(description = "Uuid of started job",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Couldn't start job", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))) })
    public ResponseEntity<String> startJob(
            @Parameter(description = "Scriptpath to run") @RequestParam String scriptFile,
            @Parameter(description = "Job priority") @RequestParam(required = false) Optional<JobPriority> priority,
            @Parameter(description = "Variables to get back from mateo") @RequestParam(required = false) Optional<List<String>> outputVariables,
            @RequestBody Optional<Map<String, String>> inputVariables) {
        String uuid = String.valueOf(jobService
                .addJob(scriptFile, priority.orElse(JobPriority.DEFAULT), inputVariables.orElseGet(HashMap::new),
                        outputVariables.orElseGet(ArrayList::new)));
        if (Objects.nonNull(uuid)) {
            return new ResponseEntity<>(uuid, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("Couldn't start job", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Get job with given uuid",
            responses = {
                    @ApiResponse(description = "Job with given uuid",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JobEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Couldn't get job", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))) })
    public ResponseEntity<JobEntity> getJob(
            @Parameter(description = "Uuid of the job to get") @RequestParam UUID uuid) {
        return jobService.getJob(uuid).map(entity -> new ResponseEntity<>(entity, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/all")
    @Operation(summary = "Get all jobs",
            responses =
            @ApiResponse(description = "List of jobs",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = JobEntity.class)))))
    public ResponseEntity<List<JobEntity>> getJobs() {
        return new ResponseEntity<>(jobService.getJobs(), HttpStatus.OK);
    }

    @GetMapping(value = "/state")
    @Operation(summary = "Get all jobs with given state",
            responses =
            @ApiResponse(description = "List of jobs",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = JobEntity.class)))))
    public ResponseEntity<Collection<JobEntity>> getJobsWithState(
            @Parameter(description = "Jobstate") @RequestParam JobStatus jobState) {
        return new ResponseEntity<>(jobService.getJobsWithState(jobState), HttpStatus.OK);
    }

    @GetMapping(value = "/state/finished")
    @Operation(summary = "Get all finished jobs",
            responses =
            @ApiResponse(description = "List of jobs",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = JobEntity.class)))))
    public ResponseEntity<Collection<JobEntity>> getFinishedJobs() {
        return new ResponseEntity<>(jobService.getJobsWithState(JobStatus.FINISHED), HttpStatus.OK);
    }

    @GetMapping(value = "/online")
    @Operation(summary = "Return online if endpoint is reachable")
    public ResponseEntity<String> isOnline() {
        return new ResponseEntity<>("online", HttpStatus.OK);
    }

    @DeleteMapping(value = "/remove")
    @Operation(summary = "Remove job with given uuid")
    public ResponseEntity<String> removeJob(@RequestParam UUID uuid) {
        jobService.removeJob(uuid);
        return new ResponseEntity<>("Job removed", HttpStatus.OK);
    }

    @DeleteMapping(value = "/remove-all")
    @Operation(summary = "Remove all jobs")
    public ResponseEntity<String> removeAllJobs() {
        jobService.clearJobList();
        return new ResponseEntity<>("All jobs removed", HttpStatus.OK);

    }

    @PostMapping(value = "/result/download/zip")
    @Operation(summary = "Download Zip with report from mateo for job with given uuid",
            responses =
            @ApiResponse(description = "Zip file",
                    content = @Content(mediaType = "application/zip",
                            array = @ArraySchema(schema = @Schema(implementation = byte[].class)))))
    public ResponseEntity<byte[]> downloadReportAsZip(@RequestParam UUID uuid) {
        Optional<JobEntity> jobOpt = jobService.getJob(uuid);

        if (jobOpt.isPresent()) {
            JobEntity job = jobOpt.get();
            byte[] zipByteStream = mateoService.downloadReport(job);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/zip"));
            String outputFilename = job.getFilename() + ".zip";
            headers.setContentDispositionFormData(outputFilename, outputFilename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            LOGGER.info("Returning zipped report...");
            return new ResponseEntity<>(zipByteStream, headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
