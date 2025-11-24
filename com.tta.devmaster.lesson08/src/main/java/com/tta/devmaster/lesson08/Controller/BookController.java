package com.tta.devmaster.lesson08.Controller;

import com.tta.devmaster.lesson08.entity.Author;
import com.tta.devmaster.lesson08.entity.Book;
import com.tta.devmaster.lesson08.Service.AuthorService;
import com.tta.devmaster.lesson08.Service.BookService;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;


    // Thư mục lưu file ảnh (nên đặt ngoài src để chạy JAR)
    private static final String UPLOAD_DIR = "uploads/images/";

    // -------------------- Hiển thị danh sách sách --------------------
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/book-list";
    }

    // -------------------- Form thêm sách mới --------------------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    @PostMapping("/save")
    public String saveBook(
            @ModelAttribute Book book,
            @RequestParam(required = false) List<Integer> authorIds,
            @RequestParam(required = false) Integer mainAuthorId,   // <-- thêm dòng này
            @RequestParam("imageBook") MultipartFile imageFile) {

        // --------- Xử lý upload ảnh giữ nguyên ----------
        Path uploadPath = Paths.get("D:/book_images/books/");

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (!imageFile.isEmpty()) {
                String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String newFileName = book.getCode() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }

                Files.copy(imageFile.getInputStream(), filePath);
                book.setImgUrl("/uploads/images/" + newFileName);
            } else if (book.getId() != null) {
                Book existingBook = bookService.getBookById(book.getId());
                book.setImgUrl(existingBook.getImgUrl());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // --------- Xử lý danh sách tác giả (checkbox) ----------
        if (authorIds != null && !authorIds.isEmpty()) {
            List<Author> authors = authorService.findAllById(authorIds);
            book.setAuthors(authors);
        }

        // --------- Xử lý CHỦ BIÊN (mainAuthorId) ----------
        if (mainAuthorId != null) {
            Author mainAuthor = authorService.getAuthorById(mainAuthorId);
            book.setMainAuthor(mainAuthor);
        } else {
            book.setMainAuthor(null); // nếu người dùng không chọn
        }

        // --------- Lưu sách ----------
        bookService.saveBook(book);
        return "redirect:/books";
    }



    // -------------------- Form sửa sách --------------------
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        return "books/book-form";
    }

    // -------------------- Xóa sách --------------------
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
