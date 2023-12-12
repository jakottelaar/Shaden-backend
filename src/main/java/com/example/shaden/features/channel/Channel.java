package com.example.shaden.features.channel;

import java.time.LocalDateTime;

import com.example.shaden.features.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class Channel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "channel_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id" , referencedColumnName = "user_id")
    private User creator;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "channel_type")
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

}
