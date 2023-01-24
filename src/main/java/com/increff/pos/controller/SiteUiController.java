package com.increff.pos.controller;

import com.increff.pos.model.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

	@Autowired
	private InfoData infoData;

	// WEBSITE PAGES
	@RequestMapping(value = "")
	public ModelAndView index() {
		if(!infoData.getEmail().isEmpty()) {
			return new ModelAndView("redirect:/ui/home");
		}
		return mav("index.html");
	}

	@RequestMapping(value = "/site/login")
	public ModelAndView login() {
		if(!infoData.getEmail().isEmpty()) {
			System.out.println(infoData.getEmail());
			return new ModelAndView("redirect:/ui/home");
		}
		return mav("login.html");
	}

	@RequestMapping(value = "/site/logout")
	public ModelAndView logout() {
		return mav("logout.html");
	}

}
