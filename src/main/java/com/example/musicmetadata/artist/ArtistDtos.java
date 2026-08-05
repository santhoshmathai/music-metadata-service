package com.example.musicmetadata.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public final class ArtistDtos {
    private ArtistDtos() {}

    public record CreateArtistRequest(
            @NotBlank @Size(max = 200) String name,
            List<@NotBlank @Size(max = 200) String> aliases) {}

    public record RenameArtistRequest(
            @NotBlank @Size(max = 200) String name,
            Boolean preservePreviousNameAsAlias) {
        public boolean shouldPreservePreviousName() {
            return preservePreviousNameAsAlias == null || preservePreviousNameAsAlias;
        }
    }

    public record ArtistResponse(UUID id, String name, List<String> aliases) {
        public static ArtistResponse from(Artist artist) {
            return new ArtistResponse(artist.getId(), artist.getName(),
                    artist.getAliases().stream().map(ArtistAlias::getName).toList());
        }
    }
}
