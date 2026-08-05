package com.example.musicmetadata.featuredartist;

import com.example.musicmetadata.artist.Artist;
import com.example.musicmetadata.artist.ArtistDtos.ArtistResponse;
import com.example.musicmetadata.artist.ArtistRepository;
import com.example.musicmetadata.common.ResourceNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ArtistOfTheDayService {
    private final ArtistDailyFeatureRepository features;
    private final ArtistRepository artists;
    private final Clock clock;
    private final MeterRegistry metrics;
    public ArtistOfTheDayService(ArtistDailyFeatureRepository features, ArtistRepository artists,
                                 Clock clock, MeterRegistry metrics) {
        this.features = features; this.artists = artists; this.clock = clock; this.metrics = metrics;
    }

    @Transactional
    public ArtistOfTheDayResponse current() {
        LocalDate today = LocalDate.now(clock);
        artists.acquireDailyRotationLock();
        ArtistDailyFeature feature = features.findById(today).orElseGet(() -> createFor(today));
        metrics.counter("music.artist_of_day.requests").increment();
        return new ArtistOfTheDayResponse(today, ArtistResponse.from(feature.getArtist()));
    }

    private ArtistDailyFeature createFor(LocalDate date) {
        List<Artist> catalogue = artists.findAllByOrderByRotationPositionAsc();
        if (catalogue.isEmpty()) {
            throw new ResourceNotFoundException("ARTIST_CATALOGUE_EMPTY",
                    "Artist of the Day is unavailable because the catalogue is empty");
        }
        Artist next = features.findFirstByFeatureDateLessThanOrderByFeatureDateDesc(date)
                .map(previous -> {
                    int previousIndex = 0;
                    for (int index = 0; index < catalogue.size(); index++) {
                        if (catalogue.get(index).getId().equals(previous.getArtist().getId())) {
                            previousIndex = index;
                            break;
                        }
                    }
                    long elapsedDays = Math.max(1, ChronoUnit.DAYS.between(previous.getFeatureDate(), date));
                    int nextIndex = (int) Math.floorMod(previousIndex + elapsedDays, catalogue.size());
                    return catalogue.get(nextIndex);
                })
                .orElse(catalogue.get(0));
        return features.save(new ArtistDailyFeature(date, next));
    }

    public record ArtistOfTheDayResponse(LocalDate date, ArtistResponse artist) {}
}
