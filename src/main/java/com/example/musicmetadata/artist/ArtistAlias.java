package com.example.musicmetadata.artist;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artist_aliases")
public class ArtistAlias {
    @Id @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
    @Column(nullable = false, length = 200)
    private String name;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ArtistAlias() {}
    ArtistAlias(Artist artist, String name) { this.artist = artist; this.name = name; }
    public String getName() { return name; }
}
