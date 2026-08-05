package com.example.musicmetadata.featuredartist;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ArtistDailyFeatureRepository extends JpaRepository<ArtistDailyFeature, LocalDate> {
    @Override
    @EntityGraph(attributePaths = {"artist", "artist.aliases"})
    Optional<ArtistDailyFeature> findById(LocalDate date);

    @EntityGraph(attributePaths = "artist")
    Optional<ArtistDailyFeature> findFirstByFeatureDateLessThanOrderByFeatureDateDesc(LocalDate date);
}
