package tn.esprit.spring.control;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tn.esprit.spring.entity.User;
import tn.esprit.spring.service.IUserService;


@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private final IUserService userService;
	@PostMapping("/create")
	public User create(@RequestBody @Valid User user) {
		userService.createOrUpdate(user);
	return user;
	}

	@GetMapping("/")
	public List<User> create() {
		return userService.getAll();
	}

	@PutMapping("/update")
	public User update(@RequestBody @Valid User user) {
		if (user.getId()==null) {
			throw new NoSuchElementException();
		}
		return userService.createOrUpdate(user);
	}


	@DeleteMapping("/delete/{id}")
	public void delete(@PathVariable("id") Long id) {
		userService.delete(id);
	}

	@ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity handleException() {
      return new ResponseEntity<>("There's no user with such ID", HttpStatus.NOT_FOUND) ;
    }

}
