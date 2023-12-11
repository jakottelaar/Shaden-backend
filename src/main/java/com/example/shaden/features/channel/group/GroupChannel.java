package com.example.shaden.features.channel.group;

import java.util.List;

import com.example.shaden.features.channel.Channel;
import com.example.shaden.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @Size(max = 10, message = "A group channel can have at most 10 users.")
    @ManyToMany
    @JoinTable(
        name = "group_channel_user",
        joinColumns = @JoinColumn(name = "group_channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
    
}
