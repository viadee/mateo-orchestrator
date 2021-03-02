package de.viadee.mateoorchestrator.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.viadee.mateoorchestrator.dtos.ReportDTO;
import de.viadee.mateoorchestrator.enums.JobPriority;
import de.viadee.mateoorchestrator.exceptions.MateoInternalServerException;
import de.viadee.mateoorchestrator.exceptions.MateoScriptAbortException;
import de.viadee.mateoorchestrator.exceptions.NoFreeMateoException;
import de.viadee.mateoorchestrator.models.JobEntity;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Marcel_Flasskamp
 */
@ExtendWith(SpringExtension.class)
@SpringJUnitConfig
@ContextConfiguration(classes = { MateoService.class, ObjectMapper.class,
        MateoServiceTest.ContextConfiguration.class })
@ActiveProfiles("test")
class MateoServiceTest {

    @Autowired
    private MateoService mateoService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FILENAME = "src/main/resources/mateoInstances.txt";

    @BeforeEach
    public void init() throws IOException {
        mateoService.blockedMateos.clear();
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
    void startScriptWithVariables() throws JsonProcessingException, MateoScriptAbortException, NoFreeMateoException,
            MateoInternalServerException, URISyntaxException {
        //given
        String scriptName = "scriptNameDummy";
        Map<String, String> inputParams = new HashMap<>();
        List<String> outputParams = new ArrayList<>();
        ReportDTO expectedReportDTO = new ReportDTO();
        expectedReportDTO.setResultString("Erfolg");
        URI uri = new URI("http://localhost:8123");
        URI uriOff = new URI("http://localhost:8124");

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(uri.toString() + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(uriOff.toString() + "/api/status", String.class))
                .thenThrow(new RestClientException("Mateo offline"));

        when(mateoService.getRestTemplate()
                .getForEntity(uri.toString() + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(uriOff.toString() + "/api/execute/is-running", Boolean.class))
                .thenThrow(new RestClientException("Mateo offline"));

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri.toString() + "/api/storage/set-all"), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("sVariable", HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri.toString() + "/api/execute/script-files?filename=" + scriptName), any(),
                        eq(String.class)))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(expectedReportDTO), HttpStatus.OK));

        ReportDTO resultDto = mateoService.startScriptWithVariables(scriptName, inputParams, outputParams);

        //then
        assertEquals(expectedReportDTO.getResultString(), resultDto.getResultString());
    }

    @Test
    void startScriptWithVariables_NoMateo() throws URISyntaxException {
        //given
        String scriptName = "scriptNameDummy";
        Map<String, String> inputParams = new HashMap<>();
        List<String> outputParams = new ArrayList<>();
        ReportDTO expectedReportDTO = new ReportDTO();
        expectedReportDTO.setResultString("Erfolg");
        URI uriOne = new URI("http://localhost:8123");
        URI uriTwo = new URI("http://localhost:8124");

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(uriOne.toString() + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.BAD_REQUEST));

        when(mateoService.getRestTemplate()
                .getForEntity(uriTwo.toString() + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.BAD_REQUEST));

        when(mateoService.getRestTemplate()
                .getForEntity(uriOne.toString() + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(uriTwo.toString() + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        Exception exception = assertThrows(NoFreeMateoException.class,
                () -> mateoService.startScriptWithVariables(scriptName, inputParams, outputParams));

        //then
        assertEquals("No free mateo", exception.getMessage());
    }

    @Test
    void testGetStorageVariables_OneVariable() throws URISyntaxException, MateoInternalServerException {
        // given
        List<String> scriptVariables = new ArrayList<>();
        scriptVariables.add("varKey");
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("varKey", "varValue");
        URI uri = new URI("http://OneVariable:8080");
        String requestUrl = uri + "/api/storage/get?key=varKey";

        //when
        when(mateoService.getRestTemplate().getForEntity(requestUrl, String.class))
                .thenReturn(new ResponseEntity<>("varValue", HttpStatus.OK));
        Map<String, String> response = mateoService.getStorageVariables(uri, scriptVariables);

        //then
        assertEquals(expectedMap, response);
    }

    @Test
    void testGetStorageVariables_MoreVariable() throws URISyntaxException, MateoInternalServerException {
        // given
        URI uri = new URI("http://MoreVariable:8080");
        List<String> scriptVariables = new ArrayList<>();
        scriptVariables.add("oneKey");
        scriptVariables.add("twoKey");
        scriptVariables.add("threeKey");
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("oneKey", "oneValue");
        expectedMap.put("twoKey", "twoValue");
        expectedMap.put("threeKey", "threeValue");
        String requestUrlOne = uri.toString() + "/api/storage/get?key=" + scriptVariables.get(0);
        String requestUrlTwo = uri.toString() + "/api/storage/get?key=" + scriptVariables.get(1);
        String requestUrlThree = uri.toString() + "/api/storage/get?key=" + scriptVariables.get(2);

        //when
        when(mateoService.getRestTemplate().getForEntity(requestUrlOne, String.class))
                .thenReturn(new ResponseEntity<>("oneValue", HttpStatus.OK));
        when(mateoService.getRestTemplate().getForEntity(requestUrlTwo, String.class))
                .thenReturn(new ResponseEntity<>("twoValue", HttpStatus.OK));
        when(mateoService.getRestTemplate().getForEntity(requestUrlThree, String.class))
                .thenReturn(new ResponseEntity<>("threeValue", HttpStatus.OK));
        Map<String, String> response = mateoService.getStorageVariables(uri, scriptVariables);

        //then
        assertEquals(expectedMap, response);
    }

    @Test
    void testGetStorageVariables_BadRequest() throws URISyntaxException {
        // given
        URI uri = new URI("http://badRequest:8080");
        List<String> scriptVariables = new ArrayList<>();
        scriptVariables.add("oKey");
        scriptVariables.add("tKey");
        scriptVariables.add("thKey");

        String requestUrlOne = uri + "/api/storage/get?key=" + scriptVariables.get(0);
        String requestUrlTwo = uri + "/api/storage/get?key=" + scriptVariables.get(1);
        String requestUrlThree = uri + "/api/storage/get?key=" + scriptVariables.get(2);

        //when
        when(mateoService.getRestTemplate().getForEntity(requestUrlOne, String.class))
                .thenReturn(new ResponseEntity<>("oneValue", HttpStatus.OK));
        when(mateoService.getRestTemplate().getForEntity(requestUrlTwo, String.class))
                .thenReturn(new ResponseEntity<>("Error while modifying storage", HttpStatus.BAD_REQUEST));
        when(mateoService.getRestTemplate().getForEntity(requestUrlThree, String.class))
                .thenReturn(new ResponseEntity<>("threeValue", HttpStatus.OK));

        Exception exception = assertThrows(MateoInternalServerException.class,
                () -> mateoService.getStorageVariables(uri, scriptVariables));

        //then
        assertEquals("Some variables couldn't be read from mateo", exception.getMessage());
    }

    @Test
    void testGetStorageVariables_NotExistingVariable() throws URISyntaxException {
        // given
        List<String> scriptVariables = new ArrayList<>();
        URI uri = new URI("http://notExisting:8080");
        scriptVariables.add("notExisting");

        String requestUrl = uri + "/api/storage/get?key=notExisting";

        //when
        when(mateoService.getRestTemplate().getForEntity(requestUrl, String.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        Exception exception = assertThrows(MateoInternalServerException.class,
                () -> mateoService.getStorageVariables(uri, scriptVariables));

        //then
        assertEquals("Some variables couldn't be read from mateo", exception.getMessage());
    }

    @Test
    void testGetStorageVariables_NotExistingVariableNoContent() throws URISyntaxException {
        // given
        List<String> scriptVariables = new ArrayList<>();
        URI uri = new URI("http://noContent:8080");
        scriptVariables.add("noContent");

        String requestUrl = uri + "/api/storage/get?key=" + scriptVariables.get(0);

        //when
        when(mateoService.getRestTemplate().getForEntity(requestUrl, String.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.NO_CONTENT));

        Exception exception = assertThrows(MateoInternalServerException.class,
                () -> mateoService.getStorageVariables(uri, scriptVariables));

        //then
        assertEquals("Some variables couldn't be read from mateo", exception.getMessage());
    }

    @Test
    void testSetStorageVariables_correctOneVariable()
            throws URISyntaxException, MateoInternalServerException {
        //given
        Map<String, String> scriptVariables = new HashMap<>();
        scriptVariables.put("sKey", "sValue");
        URI uri = new URI("http://correctOneVar:8080");
        String expectedUrl = uri.toString() + "/api/storage/set-all";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(scriptVariables, headers);

        when(mateoService.getRestTemplate()
                .postForEntity(expectedUrl, entity, String.class))
                .thenReturn(new ResponseEntity<>("sVariable", HttpStatus.OK));

        mateoService.setStorageVariables(uri, scriptVariables);

        verify(mateoService.getRestTemplate(), times(1))
                .postForEntity(expectedUrl, entity, String.class);
    }

    @Test
    void testSetStorageVariables_correctMoreVariable()
            throws URISyntaxException, MateoInternalServerException {
        //given
        URI uri = new URI("http://correctMultiVars:8080");
        Map<String, String> scriptVariables = new HashMap<>();
        scriptVariables.put("oneKey", "oneValue");
        scriptVariables.put("twoKey", "twoValue");
        scriptVariables.put("threeKey", "threeValue");
        String requestUrl = uri.toString() + "/api/storage/set-all";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(scriptVariables, headers);

        //when
        when(mateoService.getRestTemplate().postForEntity(requestUrl, entity, String.class))
                .thenReturn(new ResponseEntity<>(
                        "{\"oneKey\":\"oneValue\",\"twoKey\":\"twoValue\",\"threeKey\":\"threeValue\"}",
                        HttpStatus.OK));

        mateoService.setStorageVariables(uri, scriptVariables);

        // then
        verify(mateoService.getRestTemplate(), times(1))
                .postForEntity(requestUrl, entity, String.class);
    }

    @Test
    void testSetStorageVariables_BadRequest() throws URISyntaxException {
        //given
        URI uri = new URI("http://badRequest:8080");
        Map<String, String> scriptVariables = new HashMap<>();
        scriptVariables.put("someKey", "sValue");
        String expectedUrl = uri.toString() + "/api/storage/set-all";

        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(scriptVariables, headers);

        //when
        when(mateoService.getRestTemplate()
                .postForEntity(expectedUrl, entity, String.class))
                .thenReturn(new ResponseEntity<>("sValue", HttpStatus.BAD_REQUEST));

        Exception exception = assertThrows(MateoInternalServerException.class,
                () -> mateoService.setStorageVariables(uri, scriptVariables));

        // then
        assertEquals("Variables could not be written to the storage file.",
                exception.getMessage());
    }

    @Test
    void testStartScript()
            throws JsonProcessingException, URISyntaxException, MateoScriptAbortException,
            MateoInternalServerException {
        URI uri = new URI("http://localhost:8080");
        String filename = "startScript";
        ReportDTO expectedDTO = new ReportDTO();
        expectedDTO.setMateoInstanz(uri.toString());

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri + "/api/execute/script-files?filename=" + filename), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(expectedDTO), HttpStatus.OK));

        ReportDTO resultDto = mateoService.startScript(uri, filename);

        assertEquals(expectedDTO.toString(), resultDto.toString());
    }

    @Test
    void testStartScript_MateoScriptAbortException() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080");
        String filename = "Aborted";
        ReportDTO expectedDTO = new ReportDTO();
        expectedDTO.setMateoInstanz(uri.toString());

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri + "/api/execute/script-files?filename=" + filename), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Aborted", HttpStatus.OK));

        Exception exception = assertThrows(MateoScriptAbortException.class,
                () -> mateoService.startScript(uri, filename));

        assertEquals("Script execution aborted", exception.getMessage());
    }

    @Test
    void testStartScript_INTERNAL_SERVER_ERROR() throws URISyntaxException {
        URI uri = new URI("http://localhost:8080");
        String filename = "INTERNAL_SERVER_ERROR";
        ReportDTO expectedDTO = new ReportDTO();
        expectedDTO.setMateoInstanz(uri.toString());

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri + "/api/execute/script-files?filename=" + filename), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Some exception message", HttpStatus.INTERNAL_SERVER_ERROR));

        Exception exception = assertThrows(MateoInternalServerException.class,
                () -> mateoService.startScript(uri, filename));

        assertEquals("Some exception message", exception.getMessage());
    }

    @Test
    void testStartScript_wrongResponseDto() throws URISyntaxException, JsonProcessingException {
        URI uri = new URI("http://localhost:8080");
        String filename = "wrongResponseDto";
        JobEntity wrongDto = new JobEntity("test", JobPriority.DEFAULT,new HashMap<>(),new ArrayList<>());

        when(mateoService.getRestTemplate()
                .postForEntity(eq(uri + "/api/execute/script-files?filename=" + filename), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(objectMapper.writeValueAsString(wrongDto), HttpStatus.OK));

        Exception exception = assertThrows(JsonProcessingException.class,
                () -> mateoService.startScript(uri, filename));

        assertNotNull(exception);
    }

    @Test
    void testIsMateoOnline_Online() throws URISyntaxException {
        //given
        URI mateoUri = new URI("http://onlineMateo:8080");
        String expectedUrl = mateoUri.toString() + "/api/status";

        //when
        when(mateoService.getRestTemplate().getForEntity(expectedUrl, String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.OK));
        boolean isOnline = mateoService.isMateoOnline(mateoUri);

        //then
        assertTrue(isOnline);
    }

    @Test
    void testIsMateoOnline_Offline() throws URISyntaxException {
        //given
        URI mateoUri = new URI("http://offlineMateo:8080");
        String expectedUrl = mateoUri.toString() + "/api/status";

        //when
        when(mateoService.getRestTemplate().getForEntity(expectedUrl, String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.BAD_REQUEST));
        boolean isOnline = mateoService.isMateoOnline(mateoUri);

        //then
        assertFalse(isOnline);
    }

    @Test
    void testIsMateoRunning_Running() throws URISyntaxException {
        //given
        URI mateoUri = new URI("http://runningMateo:8080");
        String expectedUrl = mateoUri.toString() + "/api/execute/is-running";

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(expectedUrl, Boolean.class))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.OK));
        boolean isRunning = mateoService.isMateoRunning(mateoUri);

        //then
        assertTrue(isRunning);
    }

    @Test
    void testIsMateoRunning_NotRunning() throws URISyntaxException {
        //given
        URI mateoUri = new URI("http://notRunningMateo:8080");
        String expectedUrl = mateoUri.toString() + "/api/execute/is-running";

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(expectedUrl, Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));
        boolean isRunning = mateoService.isMateoRunning(mateoUri);

        //then
        assertFalse(isRunning);
    }

    @Test
    void testGetFreeMateoInstanz() throws URISyntaxException {
        //given
        URI expectedUriOnlineNotRunning = new URI("http://localhost:8123");
        URI offlineNotRunning = new URI("http://localhost:8124");

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(expectedUriOnlineNotRunning + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(offlineNotRunning + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        when(mateoService.getRestTemplate()
                .getForEntity(expectedUriOnlineNotRunning + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(offlineNotRunning + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        URI result_uri = mateoService.getFreeMateoInstanz();

        //then
        assertEquals(expectedUriOnlineNotRunning, result_uri);
    }

    @Test
    void testGetFreeMateoInstanz_AllRunning() throws URISyntaxException {
        //given
        URI runningOnlineOneBlocked = new URI("http://localhost:8123");
        URI runningOnlineTwo = new URI("http://localhost:8124");
        mateoService.blockedMateos.add(runningOnlineOneBlocked);

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(runningOnlineOneBlocked + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(eq(runningOnlineTwo + "/api/status"), eq(String.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(runningOnlineOneBlocked + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(eq(runningOnlineTwo + "/api/execute/is-running"), eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        URI result_uri = mateoService.getFreeMateoInstanz();

        //then
        assertEquals(runningOnlineTwo, result_uri);
    }

    @Test
    void testGetFreeMateoInstanz_AllOffline() throws URISyntaxException {
        //given
        URI offlineOne = new URI("http://localhost:8123");
        URI offlineTwo = new URI("http://localhost:8124");

        //when
        when(mateoService.getRestTemplate()
                .getForEntity(offlineOne + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>("Up", HttpStatus.BAD_REQUEST));

        when(mateoService.getRestTemplate()
                .getForEntity(offlineTwo + "/api/status", String.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        when(mateoService.getRestTemplate()
                .getForEntity(offlineOne + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        when(mateoService.getRestTemplate()
                .getForEntity(offlineTwo + "/api/execute/is-running", Boolean.class))
                .thenReturn(new ResponseEntity<>(false, HttpStatus.OK));

        URI result_uri = mateoService.getFreeMateoInstanz();

        //then
        assertNull(result_uri);
    }

    @Test
    void testDownloadReport() throws URISyntaxException {
        String script = "downloadScript";
        URI uri = new URI("http://mateoInstance:1234");
        JobEntity jobDTO = new JobEntity(script, JobPriority.DEFAULT,new HashMap<>(),new ArrayList<>());
        jobDTO.setMateoInstanz(uri.toString());
        jobDTO.setFilename(script);
        jobDTO.setFilePath("somePath");

        byte[] expectedByte = "SomeByte".getBytes();

        when(mateoService.getRestTemplate()
                .postForEntity(
                        eq(uri.toString() + "/api/results/zip/report-directories?absolutePaths=[%22somePath%22]"),
                        any(),
                        eq(byte[].class)))
                .thenReturn(new ResponseEntity<>(expectedByte, HttpStatus.OK));

        byte[] response = mateoService.downloadReport(jobDTO);

        assertEquals(expectedByte, response);
    }

    @Test
    void testDownloadReport_Exception() throws URISyntaxException {
        String script = "downloadScriptException";
        URI uri = new URI("http://mateoInstance:4567");
        JobEntity jobDTO = new JobEntity(script, JobPriority.DEFAULT,new HashMap<>(),new ArrayList<>());
        jobDTO.setMateoInstanz(uri.toString());
        jobDTO.setFilename(script);
        jobDTO.setFilePath("somePath");

        when(mateoService.getRestTemplate()
                .postForEntity(
                        eq(uri.toString() + "/api/results/zip/report-directories?absolutePaths=[%22somePath%22]"),
                        any(),
                        eq(byte[].class)))
                .thenThrow(new RestClientException("Error"));

        byte[] response = mateoService.downloadReport(jobDTO);

        assertTrue(response.length <= 0);
    }

    @Configuration
    static class ContextConfiguration {

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {

            RestTemplateBuilder rtb = mock(RestTemplateBuilder.class);
            RestTemplate restTemplate = mock(RestTemplate.class);

            when(rtb.build()).thenReturn(restTemplate);
            return rtb;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

}