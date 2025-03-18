package com.tasktracker.backend.entity;

import com.tasktracker.backend.kafka.listener.NewUserEventListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(NewUserEventListener.class)
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    public User(long id) {
        this.id = id;
    }

    @Transient
    private boolean isNew = true;
}
