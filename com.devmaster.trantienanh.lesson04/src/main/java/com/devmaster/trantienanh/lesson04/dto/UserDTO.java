package com.devmaster.trantienanh.lesson04.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UserDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,30}",
            message = "Password must contain at least one letter and one number"
    )
    private String password;

    @NotBlank(message = "Full name cannot be blank")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @Past(message = "Birthday must be in the past")
    private LocalDate birthday;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Pattern(
            regexp = "^\\(?[0-9]{1,4}\\)?[0-9]{7,25}$",
            message = "Phone number is invalid"
    )
    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be less than or equal to 100")
    private int age;

    @NotNull(message = "Status cannot be null")
    private Boolean status;

    // Constructor mặc định
    public UserDTO() {}

    // Constructor đầy đủ tham số
    public UserDTO(String username, String password, String fullName, LocalDate birthday,
                   String email, String phone, int age, Boolean status) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.age = age;
        this.status = status;
    }

    // Getter/Setter thủ công
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}
