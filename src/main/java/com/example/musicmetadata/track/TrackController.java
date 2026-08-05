package com.example.musicmetadata.track;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

import static com.example.musicmetadata.track.TrackDtos.*;

@RestController
@Validated
@RequestMapping("/api/v1/artists/{artistId}/tracks")
public class TrackController {
    private final TrackService service;
    public TrackController(TrackService service) { this.service = service; }

    @PostMapping
    ResponseEntity<TrackResponse> add(@PathVariable UUID artistId, @Valid @RequestBody AddTrackRequest request) {
        TrackResponse created = service.add(artistId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    Page<TrackResponse> findAll(@PathVariable UUID artistId,
                                @RequestParam(defaultValue = "0") @Min(0) int page,
                                @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size) {
        return service.findByArtist(artistId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
