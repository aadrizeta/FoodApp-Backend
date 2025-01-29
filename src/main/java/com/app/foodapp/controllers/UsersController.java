package com.app.foodapp.controllers;

import com.app.foodapp.dto.LoginRequest;
import com.app.foodapp.models.Users;
import com.app.foodapp.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/*
* localhost:8080
*
* context-path de application.properties la url queda: localhost:8080/api
*
* En este controlador la URL es: localhost:8080/api/users
* */
@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UsersController {

    @Autowired
    private UsersService usersService;

    // localhost:8080/api/users -> si no hay parámetro en GetMapping
    // localhost:8080/api/users/get-users
    @GetMapping("/get-users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = this.usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // localhost:8080/api/users/create
    @PostMapping("/create")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = usersService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest credentials){
        String respoonse = this.usersService.login(credentials.getEmail(),credentials.getPassword());
        return switch (respoonse) {
            case "El usuario no existe" -> ResponseEntity.status(404).body(Collections.singletonMap("message", "no existe"));
            case "Contreseña incorrecta" ->
                    ResponseEntity.status(401).body(Collections.singletonMap("message", "Contraseña incorrecta"));
            case "Login success" -> {
                String token = this.usersService.createToken((credentials.getEmail()));
                yield ResponseEntity.status(200).body(Collections.singletonMap("message", token));
            }
            default -> ResponseEntity.status(500).body(Collections.singletonMap("message", "error server"));
        };

    }
}
