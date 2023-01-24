package com.increff.pos.controller;

import com.increff.pos.model.InfoData;
import com.increff.pos.util.SecurityUtil;
import com.increff.pos.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class AbstractUiController {

	@Autowired
	private InfoData info;

	@Value("${app.baseUrl}")
	private String baseUrl;

	protected ModelAndView mav(String page) {

		// Get current user
		UserPrincipal principal = SecurityUtil.getPrincipal();
		System.out.println(principal.getEmail() + "yo");
		String email = principal == null ? "" : principal.getEmail();
		info.setEmail(email);
		System.out.println(info.getEmail());

		String role = getRole();
		info.setRole(role);
		System.out.println(info.getRole());

		// Set info
		ModelAndView mav = new ModelAndView(page);
		mav.addObject("info", info);
		mav.addObject("email", info.getEmail());
		mav.addObject("role", info.getRole());
		mav.addObject("baseUrl", baseUrl);
		return mav;
	}

	private static String getRole() {
		Authentication auth = SecurityUtil.getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return "";
		}

		boolean isSupervisor = auth.getAuthorities()
				.stream()
				.anyMatch(it -> it.getAuthority().equalsIgnoreCase("supervisor"));

		return isSupervisor ? "supervisor" : "operator";
	}
}