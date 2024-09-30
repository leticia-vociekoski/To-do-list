package br.com.leticiav.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name= "tb users")
public class UserModel {

    @Id
    @GeneratedValue(generator =  "UUID")
    private UUID id;

    @Getter
    @Column(unique = true)
    private String username;
    private String name;
    @Setter
    @Getter
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

}