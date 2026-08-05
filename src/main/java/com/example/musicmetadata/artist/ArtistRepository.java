package com.example.musicmetadata.artist;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
    @Override
    @EntityGraph(attributePaths = "aliases")
    Optional<Artist> findById(UUID id);

    @EntityGraph(attributePaths = "aliases")
    Optional<Artist> findFirstByRotationPositionGreaterThanOrderByRotationPositionAsc(long position);

    @EntityGraph(attributePaths = "aliases")
    Optional<Artist> findFirstByOrderByRotationPositionAsc();

    @EntityGraph(attributePaths = "aliases")
    List<Artist> findAllByOrderByRotationPositionAsc();

    @Query(value = "SELECT pg_advisory_xact_lock(7355608)", nativeQuery = true)
    void acquireDailyRotationLock();
}
