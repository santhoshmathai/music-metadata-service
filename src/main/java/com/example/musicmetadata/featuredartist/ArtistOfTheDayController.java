package com.example.musicmetadata.featuredartist;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artists/artist-of-the-day")
public class ArtistOfTheDayController {
    private final ArtistOfTheDayService service;
    public ArtistOfTheDayController(ArtistOfTheDayService service) { this.service = service; }
    @GetMapping
    ArtistOfTheDayService.ArtistOfTheDayResponse current() { return service.current(); }
}
