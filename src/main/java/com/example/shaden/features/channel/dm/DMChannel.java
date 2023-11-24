package com.example.shaden.features.channel.dm;

import java.util.List;

import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.messaging.Message;
import com.example.shaden.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "dm_channel", uniqueConstraints = @UniqueConstraint(columnNames = {"user1_id", "user2_id"}))
public class DMChannel extends Channel {

    @ManyToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "user_id")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "user_id")
    private User user2;

    @OneToMany(mappedBy = "channel")
    private List<Message> messages;
}
