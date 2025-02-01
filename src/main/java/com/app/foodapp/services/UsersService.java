package com.app.foodapp.services;

import com.app.foodapp.dto.ApiDelivery;
import com.app.foodapp.dto.LoginResponse;
import com.app.foodapp.models.Roles;
import com.app.foodapp.models.Users;
import com.app.foodapp.repositories.RolesRepository;
import com.app.foodapp.repositories.UserRepository;
import com.app.foodapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public List<Users> getAllUsers() {
        return this.userRepository.findAll();
    }

    public Optional<Users> getUserById(Long id) {
        return this.userRepository.findById(id);
    }

    public Optional<Users> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void deleteUserById(Long id) {
        Users userOptional = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        this.userRepository.delete(userOptional);
    }

    public Users updateUser(Users user, Long id) {
        Users userOptional = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        userOptional.setFirstName(user.getFirstName());
        userOptional.setLastName(user.getLastName());
        userOptional.setEmail(user.getEmail());
        userOptional.setPhone(user.getPhone());
        userOptional.setImage(user.getImage());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userOptional.setPassword(user.getPassword());
        }

        return this.userRepository.save(userOptional);
    }

    public Users createUser(Users user) {
        // Si el usuario existe lanzamos un error y no continuamos con el proceso de creación
        if (this.userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        // En caso de no existir creamos un objeto de tipo usuario en el backend y a este objeto le asignamos
        // los valores de los datos del usuario procedente del front-end
        Users newUser = new Users();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPhone(user.getPhone());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(this.passwordEncoder.encode(user.getPassword()));
        newUser.setImage("");

        // Creamos un array vacío de roles
        Set<Roles> roles = new HashSet<>();

        // Si del front no asignamos un rol por defecto, asignamos el que queramos nosotros
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // Seleccionamos el rol de la base de datos que tenga el nombre "Admin"
            Roles defaultRole = this.rolesRepository.findRoleByName("Admin");
            if (defaultRole != null) { // Si obtenemos un valor de la base de datos que coincida, lo añadimos al array roles
                roles.add(defaultRole);
            }
            else { // Si no encontramos el rol, paramos la creación del usuario
                throw new RuntimeException("No se puede agregar el rol");
            }
        }
        else {
            // En caso de que del front-end nos llegue un usuario con un array con mínimo un rol asignado
            // recorremos el array y buscamos en la base de datos por nombre cada objeto de rol y lo guardamos en el array
            for (Roles role : user.getRoles()) {
                Roles newRole = this.rolesRepository.findRoleByName(role.getName());
                roles.add(newRole);
            }
        }
        newUser.setRoles(roles); // Asignamos al objeto nuevo usuario los roles establecidos
        return this.userRepository.save(newUser); // Guardamos el usuario.
    }

    public ApiDelivery login(String email, String password) {
        //Users optionalUser = this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException());
        Optional<Users> optionalUser = this.userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            return new ApiDelivery<>("User not found", false, 404, null, "not found");
        }
        Users user = optionalUser.get();
        if (!this.passwordEncoder.matches(password, user.getPassword())){
            return new ApiDelivery("Password Incorrect", false, 400, null, "incorrect password");
        }
        String token = this.createToken(email);
        LoginResponse login = new LoginResponse(user, token);
        return new ApiDelivery("Login Success", true, 200, null, "login successful");
    }

    public String createToken(String email) {
        return this.jwtUtil.generateToken(email);
    }

}
