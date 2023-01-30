package com.increff.pos.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import com.increff.pos.util.NormalizeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.UserDao;
import com.increff.pos.pojo.UserPojo;

@Service
public class UserService {

	@Autowired
	private UserDao dao;

	@Value("${admins}")
	String adminEmails;

	private Set<String> adminEmailsSet;

	@PostConstruct
	private void init() {
		adminEmailsSet = new HashSet<>();
		for (String email : adminEmails.split(",")) {
			adminEmailsSet.add(email.trim());
		}
	}

	@Transactional
	public void add(UserPojo p) throws ApiException {
		UserPojo existing = dao.select(p.getEmail());
		if (existing != null) {
			throw new ApiException("User with given email already exists");
		}
		if (adminEmailsSet.contains(p.getEmail())) {
			p.setRole("supervisor");
		} else {
			p.setRole("operator");
		}
		dao.insert(p);
	}

	@Transactional(rollbackOn = ApiException.class)
	public UserPojo get(String email) throws ApiException {
		return dao.select(email);
	}

	@Transactional
	public List<UserPojo> getAll() {
		return dao.selectAll();
	}

	@Transactional
	public void delete(int id) {
		dao.delete(id);
	}



}



