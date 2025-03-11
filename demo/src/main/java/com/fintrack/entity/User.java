package com.fintrack.entity;

import com.fintrack.type.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;

}
