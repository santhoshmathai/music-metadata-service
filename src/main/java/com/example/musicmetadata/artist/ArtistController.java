package com.example.musicmetadata.artist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.example.musicmetadata.artist.ArtistDtos.*;

@RestController
@RequestMapping("/api/v1/artists")
@Tag(name = "Artists", description = "Create and rename catalogue artists")
public class ArtistController {
    private final ArtistService service;
    public ArtistController(ArtistService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Create an artist", description = "Creates an artist and any known aliases.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Artist created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Artist or alias already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<ArtistResponse> create(@Valid @RequestBody CreateArtistRequest request) {
        ArtistResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{artistId}")
    @Operation(summary = "Rename an artist", description = "Renames an artist, optionally retaining the previous name as an alias.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist renamed"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Artist not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Name already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ArtistResponse rename(@Parameter(description = "Artist UUID", example = "de305d54-75b4-431b-adb2-eb6b9e546014")
                          @PathVariable UUID artistId,
                          @Valid @RequestBody RenameArtistRequest request) {
        return service.rename(artistId, request);
    }
}
