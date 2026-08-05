package com.example.musicmetadata.artist;

import com.example.musicmetadata.common.ResourceNotFoundException;
import com.example.musicmetadata.common.ConflictException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

import static com.example.musicmetadata.artist.ArtistDtos.*;

@Service
public class ArtistService {
    private final ArtistRepository artists;
    private final MeterRegistry metrics;

    public ArtistService(ArtistRepository artists, MeterRegistry metrics) {
        this.artists = artists;
        this.metrics = metrics;
    }

    @Transactional
    public ArtistResponse create(CreateArtistRequest request) {
        Artist artist = new Artist(request.name());
        var uniqueAliases = new HashSet<String>();
        if (request.aliases() != null) {
            request.aliases().stream()
                    .filter(alias -> uniqueAliases.add(alias.trim().toLowerCase(Locale.ROOT)))
                    .filter(alias -> !alias.trim().equalsIgnoreCase(request.name().trim()))
                    .forEach(artist::addAlias);
        }
        Artist saved = artists.save(artist);
        metrics.counter("music.artists.created").increment();
        return ArtistResponse.from(saved);
    }

    @Transactional
    public ArtistResponse rename(UUID artistId, RenameArtistRequest request) {
        Artist artist = getArtist(artistId);
        String oldName = artist.getName();
        if (oldName.equalsIgnoreCase(request.name().trim())) return ArtistResponse.from(artist);
        if (request.shouldPreservePreviousName() && artist.getAliases().stream()
                .noneMatch(alias -> alias.getName().equalsIgnoreCase(oldName))) {
            artist.addAlias(oldName);
        }
        artist.rename(request.name());
        try {
            artists.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("ARTIST_ALIAS_CONFLICT", "The artist alias already exists");
        }
        metrics.counter("music.artists.renamed").increment();
        return ArtistResponse.from(artist);
    }

    @Transactional(readOnly = true)
    public Artist getArtist(UUID id) {
        return artists.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("ARTIST_NOT_FOUND", "No artist exists with id " + id));
    }
}
