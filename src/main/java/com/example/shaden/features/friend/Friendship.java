package com.example.shaden.features.friend;

import com.example.shaden.features.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friendship", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"friend1_id", "friend2_id"})
})
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "friend1_id", referencedColumnName = "user_id")
    private User friend1;

    @ManyToOne
    @JoinColumn(name = "friend2_id", referencedColumnName = "user_id")
    private User friend2;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

}
