package org.SP2.entities.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
@Entity
@Getter
@Setter
@Table(name="roles")
@NoArgsConstructor
public class Role {
    @Id
    @Column(name = "rolename", nullable = false)
    private String rolename;

    @ManyToMany @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> users;

    public Role(String rolename){
        this.rolename = rolename;
    }
}
