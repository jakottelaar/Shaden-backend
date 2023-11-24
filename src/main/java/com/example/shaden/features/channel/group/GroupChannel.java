package com.example.shaden.features.channel.group;

import java.util.List;

import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
@Table(name = "group_channel")
public class GroupChannel extends Channel{

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Size(max = 10, message = "A group DM can have at most 10 users.")
    @ManyToMany
    @JoinTable(
        name = "dm_channel_user",
        joinColumns = @JoinColumn(name = "dm_channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
    
}
