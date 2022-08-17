package com.seveneleven.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.seveneleven.springbatch.model.User;

public interface UserRepository extends JpaRepository<User,Integer> {

}
