package com.example.musicmetadata.track;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, UUID> {
    Page<Track> findAllByArtistId(UUID artistId, Pageable pageable);
}
