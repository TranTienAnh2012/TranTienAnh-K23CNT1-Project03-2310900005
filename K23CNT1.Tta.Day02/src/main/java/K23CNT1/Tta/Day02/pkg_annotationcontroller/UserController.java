package K23CNT1.Tta.Day02.pkg_annotationcontroller;
import org.springframework.web.bind.annotation.*;
@RestController
public class UserController {
    @GetMapping("/users")
    public String getUsers(){
        return "<h1>Get All users</h1>";
    }
    @PostMapping("/users")
    public String createUser() {
        return "<h1>Users Create</h1>";
    }
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " updated";
    }
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable int id) {
        return "<h1>User with ID " + id + " deleted";
    }
}
