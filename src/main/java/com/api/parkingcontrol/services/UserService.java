package com.api.parkingcontrol.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.parkingcontrol.models.UserModel;
import com.api.parkingcontrol.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	final UserRepository userRepository;
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Transactional
    public Object save(UserModel userModel){
        userModel.setPassword(new BCryptPasswordEncoder().encode(userModel.getPassword()));
		return userRepository.save(userModel);
    }
	
	public Page<UserModel> findAll(Pageable pageable){
        return userRepository.findAll(pageable);
    }
	
	public boolean existsUsername(String username) {
		return userRepository.existsByUsername(username);
	}
}
