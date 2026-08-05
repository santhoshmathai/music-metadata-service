package com.example.musicmetadata.track;

import com.example.musicmetadata.artist.Artist;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tracks")
public class Track {
    @Id @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
    @Column(nullable = false, length = 300)
    private String title;
    @Column(nullable = false, length = 100)
    private String genre;
    @Column(name = "duration_ms", nullable = false)
    private int durationMs;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "album_name", length = 300)
    private String albumName;
    @Column(length = 12)
    private String isrc;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Track() {}
    public Track(Artist artist, String title, String genre, int durationMs, LocalDate releaseDate,
                 String albumName, String isrc) {
        this.artist = artist;
        this.title = title.trim();
        this.genre = genre.trim();
        this.durationMs = durationMs;
        this.releaseDate = releaseDate;
        this.albumName = albumName == null ? null : albumName.trim();
        this.isrc = isrc == null || isrc.isBlank() ? null : isrc.trim().toUpperCase();
    }
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDurationMs() { return durationMs; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public String getAlbumName() { return albumName; }
    public String getIsrc() { return isrc; }
    public Instant getCreatedAt() { return createdAt; }
}
