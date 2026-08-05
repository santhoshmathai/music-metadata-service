package com.example.musicmetadata.track;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class TrackDtos {
    private TrackDtos() {}

    public record AddTrackRequest(
            @Schema(example = "Around the World") @NotBlank @Size(max = 300) String title,
            @Schema(example = "House") @NotBlank @Size(max = 100) String genre,
            @Schema(description = "Duration in milliseconds", example = "429533") @Min(1) @Max(86400000) int durationMs,
            @Schema(example = "1997-03-17") @PastOrPresent LocalDate releaseDate,
            @Schema(example = "Homework") @Size(max = 300) String albumName,
            @Schema(description = "12-character International Standard Recording Code", example = "GBDUW0000059")
            @Pattern(regexp = "^[A-Za-z]{2}[A-Za-z0-9]{3}[0-9]{7}$", message = "must be a valid 12-character ISRC") String isrc) {}

    @Schema(description = "Track metadata")
    public record TrackResponse(UUID id, String title, String genre, int durationMs,
                                LocalDate releaseDate, String albumName, String isrc, Instant createdAt) {
        static TrackResponse from(Track track) {
            return new TrackResponse(track.getId(), track.getTitle(), track.getGenre(), track.getDurationMs(),
                    track.getReleaseDate(), track.getAlbumName(), track.getIsrc(), track.getCreatedAt());
        }
    }
}
