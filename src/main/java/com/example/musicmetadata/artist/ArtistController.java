package com.example.musicmetadata.artist;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.example.musicmetadata.artist.ArtistDtos.*;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {
    private final ArtistService service;
    public ArtistController(ArtistService service) { this.service = service; }

    @PostMapping
    ResponseEntity<ArtistResponse> create(@Valid @RequestBody CreateArtistRequest request) {
        ArtistResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{artistId}")
    ArtistResponse rename(@PathVariable UUID artistId, @Valid @RequestBody RenameArtistRequest request) {
        return service.rename(artistId, request);
    }
}
