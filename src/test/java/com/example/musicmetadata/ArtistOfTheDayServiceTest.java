package com.example.musicmetadata;

import com.example.musicmetadata.artist.Artist;
import com.example.musicmetadata.artist.ArtistRepository;
import com.example.musicmetadata.featuredartist.ArtistDailyFeature;
import com.example.musicmetadata.featuredartist.ArtistDailyFeatureRepository;
import com.example.musicmetadata.featuredartist.ArtistOfTheDayService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistOfTheDayServiceTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 8, 5);
    @Mock ArtistDailyFeatureRepository features;
    @Mock ArtistRepository artists;
    private ArtistOfTheDayService service;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        service = new ArtistOfTheDayService(features, artists, clock, new SimpleMeterRegistry());
        when(features.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void startsWithTheFirstArtistAndPersistsTheDailyChoice() {
        Artist first = artist("First");
        Artist second = artist("Second");
        when(features.findById(TODAY)).thenReturn(Optional.empty());
        when(features.findFirstByFeatureDateLessThanOrderByFeatureDateDesc(TODAY)).thenReturn(Optional.empty());
        when(artists.findAllByOrderByRotationPositionAsc()).thenReturn(List.of(first, second));

        var result = service.current();

        assertThat(result.artist().name()).isEqualTo("First");
        verify(artists).acquireDailyRotationLock();
        verify(features).save(any(ArtistDailyFeature.class));
    }

    @Test
    void advancesByElapsedUtcDaysAndWrapsAround() {
        Artist first = artist("First");
        Artist second = artist("Second");
        Artist third = artist("Third");
        ArtistDailyFeature previous = mock(ArtistDailyFeature.class);
        when(previous.getFeatureDate()).thenReturn(TODAY.minusDays(2));
        when(previous.getArtist()).thenReturn(second);
        when(features.findById(TODAY)).thenReturn(Optional.empty());
        when(features.findFirstByFeatureDateLessThanOrderByFeatureDateDesc(TODAY)).thenReturn(Optional.of(previous));
        when(artists.findAllByOrderByRotationPositionAsc()).thenReturn(List.of(first, second, third));

        var result = service.current();

        assertThat(result.artist().name()).isEqualTo("First");
    }

    private Artist artist(String name) {
        Artist artist = mock(Artist.class);
        lenient().when(artist.getId()).thenReturn(UUID.randomUUID());
        lenient().when(artist.getName()).thenReturn(name);
        lenient().when(artist.getAliases()).thenReturn(Set.of());
        return artist;
    }
}
