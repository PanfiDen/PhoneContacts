package com.chiacademy.phonecontacts.user.repository;

import com.chiacademy.phonecontacts.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
