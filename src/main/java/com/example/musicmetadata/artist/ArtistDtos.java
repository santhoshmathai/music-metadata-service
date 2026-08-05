package com.example.musicmetadata.artist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public final class ArtistDtos {
    private ArtistDtos() {}

    public record CreateArtistRequest(
            @Schema(description = "Canonical artist name", example = "Daft Punk")
            @NotBlank @Size(max = 200) String name,
            @Schema(description = "Alternative or historical artist names", example = "[\"Darlin'\"]")
            List<@NotBlank @Size(max = 200) String> aliases) {}

    public record RenameArtistRequest(
            @Schema(description = "New canonical name", example = "Daft Punk Robots")
            @NotBlank @Size(max = 200) String name,
            @Schema(description = "Retain the old name as an alias; defaults to true", defaultValue = "true")
            Boolean preservePreviousNameAsAlias) {
        public boolean shouldPreservePreviousName() {
            return preservePreviousNameAsAlias == null || preservePreviousNameAsAlias;
        }
    }

    @Schema(description = "Artist metadata")
    public record ArtistResponse(
            @Schema(example = "de305d54-75b4-431b-adb2-eb6b9e546014") UUID id,
            @Schema(example = "Daft Punk") String name,
            List<String> aliases) {
        public static ArtistResponse from(Artist artist) {
            return new ArtistResponse(artist.getId(), artist.getName(),
                    artist.getAliases().stream().map(ArtistAlias::getName).toList());
        }
    }
}
