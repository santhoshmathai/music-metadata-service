package com.example.musicmetadata.track;

import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class TrackDtos {
    private TrackDtos() {}

    public record AddTrackRequest(
            @NotBlank @Size(max = 300) String title,
            @NotBlank @Size(max = 100) String genre,
            @Min(1) @Max(86400000) int durationMs,
            @PastOrPresent LocalDate releaseDate,
            @Size(max = 300) String albumName,
            @Pattern(regexp = "^[A-Za-z]{2}[A-Za-z0-9]{3}[0-9]{7}$", message = "must be a valid 12-character ISRC") String isrc) {}

    public record TrackResponse(UUID id, String title, String genre, int durationMs,
                                LocalDate releaseDate, String albumName, String isrc, Instant createdAt) {
        static TrackResponse from(Track track) {
            return new TrackResponse(track.getId(), track.getTitle(), track.getGenre(), track.getDurationMs(),
                    track.getReleaseDate(), track.getAlbumName(), track.getIsrc(), track.getCreatedAt());
        }
    }
}
