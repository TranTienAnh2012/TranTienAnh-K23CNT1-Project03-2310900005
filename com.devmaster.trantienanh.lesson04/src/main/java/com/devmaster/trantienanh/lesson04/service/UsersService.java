package com.devmaster.trantienanh.lesson04.service;

import com.devmaster.trantienanh.lesson04.dto.UserDTO;
import com.devmaster.trantienanh.lesson04.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {

    private final List<User> userList = new ArrayList<>();

    public UsersService() {
        // User 1
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("pass1234");
        user1.setFullName("John Doe");
        user1.setBirthday(LocalDate.parse("1990-01-01"));
        user1.setEmail("john@example.com");
        user1.setPhone("1234567890");
        user1.setAge(34);
        user1.setStatus(true);
        userList.add(user1);

        // User 2
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("pass5678");
        user2.setFullName("Jane Smith");
        user2.setBirthday(LocalDate.parse("1992-05-15"));
        user2.setEmail("jane@example.com");
        user2.setPhone("0987654321");
        user2.setAge(32);
        user2.setStatus(false);
        userList.add(user2);
    }

    // Lấy danh sách tất cả user
    public List<User> findAll() {
        return new ArrayList<>(userList);
    }

    // Tạo user mới từ UserDTO
    public boolean create(UserDTO userDTO) {
        if (userDTO == null) return false;

        try {
            User user = new User();
            user.setId((long) (userList.size() + 1));
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setFullName(userDTO.getFullName());
            user.setBirthday(userDTO.getBirthday());
            user.setEmail(userDTO.getEmail());
            user.setPhone(userDTO.getPhone());
            user.setAge(userDTO.getAge());
            user.setStatus(userDTO.getStatus());

            userList.add(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
