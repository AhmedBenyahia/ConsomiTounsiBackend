package tn.esprit.spring.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import tn.esprit.spring.entity.User;
import tn.esprit.spring.repository.IUserRepository;
import tn.esprit.spring.service.IUserService;

@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService{

	private final IUserRepository userRepository;
	@Override
	public User createOrUpdate(User user) {
		if (user.getId()!=null) 
		{
				getById(user.getId());
		}
		return userRepository.save(user);
	}

	@Override
	public void delete(Long id) {
		userRepository.deleteById(id);
		
	}

	@Override
	public User getById(Long id) throws NoSuchElementException {
		Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) return user.get();
		throw new NoSuchElementException();
	}
	
	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByLogin(username).orElse(null);
	}

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

}
