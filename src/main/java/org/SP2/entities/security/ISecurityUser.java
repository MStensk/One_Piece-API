package org.SP2.entities.security;

public interface ISecurityUser {
    boolean verifyPassword(String pw);
    void addRole(Role role);
}
