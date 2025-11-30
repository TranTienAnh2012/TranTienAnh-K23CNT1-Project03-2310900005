package com.devmaster.tta_lesson06.controller;

import com.devmaster.tta_lesson06.dto.StudentDTO;
import com.devmaster.tta_lesson06.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // List all students
    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/student-list";
    }

    // Show form to add a new student
    @GetMapping("/add-new")
    public String showAddForm(Model model) {
        model.addAttribute("student", new StudentDTO());
        return "students/student-add";
    }

    // Save a new student
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute("student") StudentDTO student) {
        studentService.save(student);
        return "redirect:/students";
    }

    // Show form to edit an existing student
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StudentDTO student = studentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id: " + id));
        model.addAttribute("student", student);
        return "students/student-edit";
    }

    // Update an existing student
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") StudentDTO student) {
        studentService.updateStudentById(id, student);
        return "redirect:/students";
    }

    // Delete a student
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}
