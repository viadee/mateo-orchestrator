package de.viadee.mateoorchestrator.controller;

import de.viadee.mateoorchestrator.MateoInstances;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

/**
 * @author Marcel_Flasskamp
 */
@Controller
@RequestMapping({ "/api/mateo" })
@Tag(name = "Mateo API", description = "Rest endpoint for mateo-instances")
public class MateoApiController {

    @GetMapping(value = "/all")
    @Operation(summary = "Get all mateos",
            responses = @ApiResponse(description = "List of mateos",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = URI.class))
                    )))
    public ResponseEntity<Set<URI>> getMateos() {
        return new ResponseEntity<>(MateoInstances.getMateos(), HttpStatus.OK);
    }

    @PostMapping(value = "/add")
    @Operation(summary = "Add mateo instance",
            responses = {
                    @ApiResponse(description = "Added mateo-url",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "304", description = "Mateo already exists"),
                    @ApiResponse(responseCode = "400", description = "No valid URL", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))) })
    public ResponseEntity<String> addMateoInstance(
            @Parameter(description = "mateo-url which will be added to list", schema = @Schema(format = "URI")) @RequestParam(value = "mateoUrl") String mateoUriString) {
        try {
            URI mateoUri = new URL(mateoUriString).toURI();

            if (MateoInstances.addMateo(mateoUri))
                return new ResponseEntity<>(mateoUriString, HttpStatus.CREATED);
            else
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (URISyntaxException | MalformedURLException e) {
            return new ResponseEntity<>(String.format("Invalid mateo Url '%s' : %s", mateoUriString, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(String.format("Couldn't write Mateo Url %s", mateoUriString),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/remove")
    @Operation(summary = "Remove given mateo from mateo list",
            responses = {
                    @ApiResponse(description = "The removed mateo URL",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "304", description = "Mateo doesn't exists"),
                    @ApiResponse(responseCode = "400", description = "No valid URL", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))) })
    public ResponseEntity<String> deleteMateoInstance(
            @Parameter(description = "mateo-url which will be removed from list", schema = @Schema(format = "URI")) @RequestParam(value = "mateoUrl") String mateoUriString) {
        try {
            URI mateoUri = new URL(mateoUriString).toURI();

            if (MateoInstances.removeMateo(mateoUri))
                return new ResponseEntity<>(mateoUriString, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (URISyntaxException | MalformedURLException e) {
            return new ResponseEntity<>(String.format("Invalid mateo Url '%s' : %s", mateoUriString, e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(String.format("Couldn't delete Mateo Url %s", mateoUriString),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/remove-all")
    @Operation(summary = "Remove all mateos",
            responses = {
                    @ApiResponse(description = "The removed mateo URL",
                            content = @Content(mediaType = "text/plain",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "304", description = "File doesn't exists"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))) })
    public ResponseEntity<String> deleteAllMateoInstance() {
        try {
            if (MateoInstances.removeAll())
                return new ResponseEntity<>("Deleted all", HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
