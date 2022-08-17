package com.seveneleven.springbatch.config;

import org.springframework.batch.item.ItemProcessor;

import com.seveneleven.springbatch.model.User;


public class UserProcessor implements ItemProcessor<User, User>{

	@Override
	public User process(User user) throws Exception {
		return user;
	}

}
