package de.viadee.mateoorchestrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.viadee.mateoorchestrator.MateoInstances;
import de.viadee.mateoorchestrator.dtos.ReportDTO;
import de.viadee.mateoorchestrator.exceptions.MateoInternalServerException;
import de.viadee.mateoorchestrator.exceptions.MateoScriptAbortException;
import de.viadee.mateoorchestrator.exceptions.NoFreeMateoException;
import de.viadee.mateoorchestrator.models.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Marcel_Flasskamp
 */
@Service
public class MateoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MateoService.class);

    private static final String API_STATUS = "api/status";

    private static final String API_MATEO_IS_RUNNING = "api/execute/is-running";

    private static final String API_EXECUTE_SCRIPT_FILES = "api/execute/script-files";

    private static final String API_STORAGE_SET_ALL = "api/storage/set-all";

    private static final String API_STORAGE_GET = "api/storage/get";

    private static final String API_RESULTS_ZIP_REPORT_DIRECTORIES = "/api/results/zip/report-directories";

    private static final String PARAM_FILENAME = "filename";

    private static final String PARAM_KEY = "key";

    private static final String PARAM_ABSOLUTE_PATHS = "absolutePaths";

    public static final String PATH_DELIMITER = "/";

    public static final String ABORTED = "Aborted";

    private static final String NOT_FOUND = "notFound";

    private final RestTemplate restTemplate;

    /**
     * Represents the mateo instances blocked by mateo service
     */
    protected final Set<URI> blockedMateos = new HashSet<>();

    public MateoService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * First, a free mateo instance is searched for. If one is available, it is set as occupied.
     * Then the transferred variables are written to the storage of the mateo instance,
     * the script is started,
     * the variables are read from the storage
     * and then the mateo instance is released again.
     *
     * @param scriptName      path of the script to run (script has to be in the environment script directory of mateo)
     * @param inputVariables  input variables for the testscript
     * @param outputVariables output variables from the testscript
     * @return response from camunda (reportDTO)
     */
    public ReportDTO startScriptWithVariables(String scriptName, Map<String, String> inputVariables,
            List<String> outputVariables)
            throws JsonProcessingException, NoFreeMateoException, MateoScriptAbortException,
            MateoInternalServerException {
        URI mateoUri = getFreeMateoInstanz();
        if (mateoUri != null) {
            blockedMateos.add(mateoUri);
            LOGGER.info("Start script with mateo: {}", mateoUri);
            setStorageVariables(mateoUri, inputVariables);
            ReportDTO reportDTO = startScript(mateoUri, scriptName);
            reportDTO.setOutputVariables(getStorageVariables(mateoUri, outputVariables));
            blockedMateos.remove(mateoUri);
            reportDTO.setMateoInstanz(String.valueOf(mateoUri));
            return reportDTO;
        } else {
            throw new NoFreeMateoException("No free mateo");
        }
    }

    /**
     * get Value of storage variables (uses mateo rest-api)
     *
     * @param mateoUri        uri of the mateo instance
     * @param resultVariables List of keys to get the values from mateo
     * @return HashMap of storage variables mit keys from inputParameter and values from mateo (storage)
     */
    protected Map<String, String> getStorageVariables(URI mateoUri, List<String> resultVariables)
            throws MateoInternalServerException {
        Map<String, String> resultMap = new HashMap<>();
        boolean failed = false;
        ResponseEntity<String> responseEntity;
        LOGGER.info("Read storage variables...");
        for (String variable : resultVariables) {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(mateoUri.getScheme())
                    .host(mateoUri.getHost())
                    .port(mateoUri.getPort())
                    .path(API_STORAGE_GET)
                    .queryParam(PARAM_KEY, variable)
                    .build()
                    .toUri();
            responseEntity = restTemplate.getForEntity(uri.toString(), String.class);
            if (responseEntity.getStatusCodeValue() >= 300 || responseEntity.getStatusCode()
                    .equals(HttpStatus.NO_CONTENT) || Objects.isNull(responseEntity.getBody())) {
                LOGGER.warn("Variable: {} could not be read", variable);
                failed = true;
                resultMap.put(variable, NOT_FOUND);
            } else {
                resultMap.put(variable, responseEntity.getBody());
            }
        }
        if (failed){
            blockedMateos.remove(mateoUri);
            throw new MateoInternalServerException("Some variables couldn't be read from mateo");
        }
        LOGGER.info("Storage variables read out: {}", resultMap);
        return resultMap;
    }

    /**
     * Write variables (key, value) to the mateo storage (uses mateo rest-api)
     *
     * @param mateoUri        uri of the mateo instance
     * @param scriptVariables Map of variables to write to storage
     */
    protected void setStorageVariables(URI mateoUri, Map<String, String> scriptVariables)
            throws MateoInternalServerException {
        LOGGER.info("Write variables to storage: {}", scriptVariables);

        URI uri = UriComponentsBuilder.newInstance()
                .scheme(mateoUri.getScheme())
                .host(mateoUri.getHost())
                .port(mateoUri.getPort())
                .path(API_STORAGE_SET_ALL)
                .build()
                .toUri();
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(scriptVariables, headers);
        ResponseEntity<String> response = this.restTemplate.postForEntity(uri.toString(), entity, String.class);

        if (response.getStatusCodeValue() >= 300) {
            blockedMateos.remove(mateoUri);
            throw new MateoInternalServerException("Variables could not be written to the storage file.");
        }
    }

    /**
     * start mateo script via mateo rest-api
     *
     * @param mateoUri   uri of the mateo instance
     * @param scriptName path of the script to run (script has to be in the environment script directory of mateo)
     * @return response from camunda (reportDTO)
     */
    protected ReportDTO startScript(URI mateoUri, String scriptName)
            throws MateoScriptAbortException, MateoInternalServerException,
            JsonProcessingException {
        LOGGER.info("Run the script: {}", scriptName);
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(mateoUri.getScheme())
                .host(mateoUri.getHost())
                .port(mateoUri.getPort())
                .path(API_EXECUTE_SCRIPT_FILES)
                .queryParam(PARAM_FILENAME, scriptName)
                .build()
                .toUri();
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = this.restTemplate
                .postForEntity(uri.toString(), entity, String.class);

        if (response.hasBody() && Objects.equals(response.getBody(), ABORTED)) {
            blockedMateos.remove(mateoUri);
            throw new MateoScriptAbortException("Script execution aborted");
        }

        if (response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            blockedMateos.remove(mateoUri);
            throw new MateoInternalServerException(response.getBody());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), ReportDTO.class);
    }

    /**
     * download report as zip for given job
     *
     * @param job job to get report
     * @return byte[] of report zip
     */
    public byte[] downloadReport(JobEntity job) {
        String filePathWithoutFile = job.getFilePath()
                .replace(PATH_DELIMITER + job.getFilename(), "");
        String absolutePath = String.format("%s%s%s", "[\"", filePathWithoutFile, "\"]");
        try {
            LOGGER.info("Download Zip: ");
            URI mateoInstance = new URI(job.getMateoInstanz());
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(mateoInstance.getScheme())
                    .host(mateoInstance.getHost())
                    .port(mateoInstance.getPort())
                    .path(API_RESULTS_ZIP_REPORT_DIRECTORIES)
                    .queryParam(PARAM_ABSOLUTE_PATHS, absolutePath)
                    .build()
                    .toUri();
            HttpHeaders headers = getHttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(null, headers);

            ResponseEntity<byte[]> response = this.restTemplate
                    .postForEntity(uri.toString(), entity, byte[].class);

            return response.getBody();
        } catch (RestClientException | URISyntaxException e) {
            LOGGER.error("Zip konnte nicht geladen werden", e);
        }
        return new byte[] {};
    }

    /**
     * check if mateo is reachable/online
     *
     * @param mateoUri uri of the mateo instance
     * @return boolean if mateo is online
     */
    protected boolean isMateoOnline(URI mateoUri) {
        try {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(mateoUri.getScheme())
                    .host(mateoUri.getHost())
                    .port(mateoUri.getPort())
                    .path(API_STATUS)
                    .build()
                    .toUri();

            ResponseEntity<String> response = restTemplate
                    .getForEntity(uri.toString(), String.class);
            if (response.getStatusCodeValue() >= 300) {
                LOGGER.error("mateo status: {} http StatusCode: {}", response.getBody(), response.getStatusCode());
                return false;
            }
        } catch (RestClientException e) {
            LOGGER.error("mateo is offline: {}", mateoUri);
            return false;
        }
        return true;
    }

    /**
     * check if mateo is running
     *
     * @param mateoUri uri of the mateo instance
     * @return boolean if mateo is running
     */
    protected boolean isMateoRunning(URI mateoUri) {
        try {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme(mateoUri.getScheme())
                    .host(mateoUri.getHost())
                    .port(mateoUri.getPort())
                    .path(API_MATEO_IS_RUNNING)
                    .build()
                    .toUri();
            ResponseEntity<Boolean> response = restTemplate.getForEntity(uri.toString(), Boolean.class);
            boolean running = response.getBody() != null ? response.getBody() : true;
            LOGGER.info("mateo '{}' {}", mateoUri, (running ? " is running" : " is free"));
            return running;
        } catch (RestClientException e) {
            return false;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    /**
     * Find a not running or blocked mateo instance
     * If all mateos are running or blocked, return da non blocked mateo
     * If all blocked and running, return null
     *
     * @return uri of free mateo or null if no mateo is free
     */
    protected URI getFreeMateoInstanz() {
        LOGGER.info("Search for free mateo instance...");
        return MateoInstances.getMateos().stream()
                .filter(mateoUri -> isMateoOnline(mateoUri) && !isMateoRunning(mateoUri) && !blockedMateos
                        .contains(mateoUri)) // find a online, not running or blocked mateo
                .findAny()
                .orElseGet(() -> MateoInstances.getMateos().stream()
                        .filter(mateoUri -> isMateoOnline(mateoUri) && !blockedMateos
                                .contains(mateoUri)) // find a online not blocked mateo
                        .findAny()
                        .orElse(null));
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
}
