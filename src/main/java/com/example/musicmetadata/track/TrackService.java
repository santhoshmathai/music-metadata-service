package com.example.musicmetadata.track;

import com.example.musicmetadata.artist.Artist;
import com.example.musicmetadata.artist.ArtistService;
import com.example.musicmetadata.common.ConflictException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.example.musicmetadata.track.TrackDtos.*;

@Service
public class TrackService {
    private final TrackRepository tracks;
    private final ArtistService artists;
    private final MeterRegistry metrics;
    public TrackService(TrackRepository tracks, ArtistService artists, MeterRegistry metrics) {
        this.tracks = tracks; this.artists = artists; this.metrics = metrics;
    }

    @Transactional
    public TrackResponse add(UUID artistId, AddTrackRequest request) {
        Artist artist = artists.getArtist(artistId);
        Track track = new Track(artist, request.title(), request.genre(), request.durationMs(),
                request.releaseDate(), request.albumName(), request.isrc());
        try {
            Track saved = tracks.saveAndFlush(track);
            metrics.counter("music.tracks.created").increment();
            return TrackResponse.from(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("TRACK_ISRC_CONFLICT", "A track with this ISRC already exists");
        }
    }

    @Transactional(readOnly = true)
    public Page<TrackResponse> findByArtist(UUID artistId, Pageable pageable) {
        artists.getArtist(artistId);
        return tracks.findAllByArtistId(artistId, pageable).map(TrackResponse::from);
    }
}
