package com.example.musicmetadata.featuredartist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artists/artist-of-the-day")
@Tag(name = "Artist of the Day", description = "Daily fair artist rotation")
public class ArtistOfTheDayController {
    private final ArtistOfTheDayService service;
    public ArtistOfTheDayController(ArtistOfTheDayService service) { this.service = service; }
    @GetMapping
    @Operation(summary = "Get today's featured artist", description = "Returns the artist selected for the current UTC date.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Featured artist"),
            @ApiResponse(responseCode = "404", description = "The artist catalogue is empty", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    ArtistOfTheDayService.ArtistOfTheDayResponse current() { return service.current(); }
}
