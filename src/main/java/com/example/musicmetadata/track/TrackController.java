package com.example.musicmetadata.track;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

import static com.example.musicmetadata.track.TrackDtos.*;

@RestController
@Validated
@RequestMapping("/api/v1/artists/{artistId}/tracks")
@Tag(name = "Tracks", description = "Manage tracks belonging to an artist")
public class TrackController {
    private final TrackService service;
    public TrackController(TrackService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Add a track to an artist")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Track created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Artist not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "ISRC already exists", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ResponseEntity<TrackResponse> add(@Parameter(description = "Artist UUID") @PathVariable UUID artistId,
                                      @Valid @RequestBody AddTrackRequest request) {
        TrackResponse created = service.add(artistId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "List an artist's tracks", description = "Returns tracks newest first using zero-based pagination.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of tracks"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Artist not found", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    Page<TrackResponse> findAll(@Parameter(description = "Artist UUID") @PathVariable UUID artistId,
                                @Parameter(description = "Zero-based page number") @RequestParam(defaultValue = "0") @Min(0) int page,
                                @Parameter(description = "Page size (1-200)") @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        return service.findByArtist(artistId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
