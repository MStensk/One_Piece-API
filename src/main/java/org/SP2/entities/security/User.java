package org.SP2.entities.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User{

    @Id
    @Column(name = "username", nullable = false)
    private String username;

    private String password;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
        this.username = username;
        this.password = hashed;
    }

    public boolean checkPassword(String candidate){
        if (BCrypt.checkpw(candidate, password))
            return true;
        else
            return false;
    }

    public void addRole(Role role){
        this.roles.add(role);
        role.getUsers().add(this);
    }
}
