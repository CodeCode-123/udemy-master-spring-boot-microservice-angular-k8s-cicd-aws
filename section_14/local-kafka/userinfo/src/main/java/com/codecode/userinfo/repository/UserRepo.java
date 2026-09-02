package com.codecode.userinfo.repository;

import com.codecode.userinfo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
}
