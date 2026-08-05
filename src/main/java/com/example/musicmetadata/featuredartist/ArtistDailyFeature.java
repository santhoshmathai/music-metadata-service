package com.example.musicmetadata.featuredartist;

import com.example.musicmetadata.artist.Artist;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "artist_daily_features")
public class ArtistDailyFeature {
    @Id @Column(name = "feature_date")
    private LocalDate featureDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ArtistDailyFeature() {}
    public ArtistDailyFeature(LocalDate featureDate, Artist artist) {
        this.featureDate = featureDate; this.artist = artist;
    }
    public LocalDate getFeatureDate() { return featureDate; }
    public Artist getArtist() { return artist; }
}
