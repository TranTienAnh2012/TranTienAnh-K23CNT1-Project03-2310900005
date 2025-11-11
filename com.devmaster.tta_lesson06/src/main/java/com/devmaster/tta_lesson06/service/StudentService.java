package com.devmaster.tta_lesson06.service;

import com.devmaster.tta_lesson06.dto.StudentDTO;
import com.devmaster.tta_lesson06.entity.Student;
import com.devmaster.tta_lesson06.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // tự tạo constructor với các final fields
public class StudentService {

    private final StudentRepository studentRepository;

    // Lấy tất cả student
    public List<StudentDTO> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy student theo ID
    public Optional<StudentDTO> findById(Long id) {
        return studentRepository.findById(id)
                .map(this::convertToDTO);
    }

    // Lưu student mới
    public StudentDTO save(StudentDTO studentDTO) {
        Student student = convertToEntity(studentDTO);
        Student saved = studentRepository.save(student);
        return convertToDTO(saved);
    }

    // Cập nhật student theo ID
    public StudentDTO updateStudentById(Long id, StudentDTO updatedStudent) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student ID: " + id));

        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        student.setAge(updatedStudent.getAge());

        Student saved = studentRepository.save(student);
        return convertToDTO(saved);
    }

    // Xóa student
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid student ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    // Helper: chuyển entity -> DTO
    private StudentDTO convertToDTO(Student student) {
        return StudentDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .age(student.getAge())
                .build();
    }

    // Helper: chuyển DTO -> entity
    private Student convertToEntity(StudentDTO dto) {
        return Student.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .build();
    }
}
