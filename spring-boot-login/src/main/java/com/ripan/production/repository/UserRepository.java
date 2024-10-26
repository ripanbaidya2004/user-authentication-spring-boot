package com.ripan.production.repository;

import com.ripan.production.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

     User findByEmail(String email);
}
