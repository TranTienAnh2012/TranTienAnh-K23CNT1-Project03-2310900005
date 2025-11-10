package com.devmaster.lession03.service;

import com.devmaster.lession03.entity.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service class: ServiceStudent
 * <p>Lớp dịch vụ thực hiện các chức năng thao tác với List Object Student</p>
 * @Name Trần Tiến Anh
 * @Date 10/11/2025
 * @author
 * @version 1.1
 */
@Service
public class ServiceStudent {
    private List<Student> students = new ArrayList<>();

    public ServiceStudent() {
        students.addAll(Arrays.asList(
                new Student(1L, "Devmaster 1", 20, "Nam", "Số 25", "0978611889", "tienanhtran755@gmail.com"),
                new Student(2L, "Devmaster 2", 25, "Nam", "Số 25 VNP", "0978611889", "contact@devmaster.edu.vn"),
                new Student(3L, "Devmaster 3", 22, "Nam", "Số 25 VNP", "0978611889", "chungtrinhj@gmail.com")
        ));
    }

    // Lấy toàn bộ danh sách sinh viên
    public List<Student> getAllStudents() {
        return students;
    }

    // Lấy sinh viên theo id
    public Student getStudent(Long id) {
        return students.stream()
                .filter(student -> student.getTtaId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Thêm mới một sinh viên
    public Student addStudent(Student student) {
        students.add(student);
        return student;
    }

    // Cập nhật thông tin sinh viên
    public Student updateStudent(Long id, Student student) {
        Student check = getStudent(id);
        if (check == null) {
            return null;
        }
        students.forEach(item -> {
            if (item.getTtaId().equals(id)) {
                item.setTtaName(student.getTtaName());
                item.setTtaAddress(student.getTtaAddress());
                item.setTtaEmail(student.getTtaEmail());
                item.setTtaPhone(student.getTtaPhone());
                item.setTtaAge(student.getTtaAge());
                item.setTtaGender(student.getTtaGender());
            }
        });
        return student;
    }

    // Xóa sinh viên theo id
    public boolean deleteStudent(Long id) {
        Student check = getStudent(id);
        return students.remove(check);
    }
}
