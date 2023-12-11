package com.example.shaden.features.channel.dm;

import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dm_channel", uniqueConstraints = @UniqueConstraint(columnNames = {"creator_id", "participant_id"}))
public class DMChannel extends Channel {

    @ManyToOne
    @JoinColumn(name = "participant_id", referencedColumnName = "user_id")
    private User participant;

}
