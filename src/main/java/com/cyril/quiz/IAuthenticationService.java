package com.cyril.quiz;

public interface IAuthenticationService {
    boolean login(String username, String password);
    boolean signup(String username, String password);
}