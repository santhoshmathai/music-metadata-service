package com.example.musicmetadata.artist;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artists")
public class Artist {
    @Id @UuidGenerator
    private UUID id;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(name = "rotation_position", insertable = false, updatable = false, nullable = false)
    private Long rotationPosition;
    @Version
    private long version;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private Set<ArtistAlias> aliases = new LinkedHashSet<>();

    protected Artist() {}
    public Artist(String name) { this.name = name.trim(); }
    public void rename(String newName) { this.name = newName.trim(); }
    public void addAlias(String alias) { aliases.add(new ArtistAlias(this, alias.trim())); }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Long getRotationPosition() { return rotationPosition; }
    public Set<ArtistAlias> getAliases() { return aliases; }
}
