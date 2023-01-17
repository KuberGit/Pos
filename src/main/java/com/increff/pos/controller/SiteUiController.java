package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

	// WEBSITE PAGES
	@RequestMapping(value = "")
	public ModelAndView index() {
		return mav("index.html");
	}

	@RequestMapping(value = "/site/brand")
	public ModelAndView brand() {
		return mav("brand.html");
	}

	@RequestMapping(value = "/site/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}

	@RequestMapping(value = "/site/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/site/order")
	public ModelAndView order() {
		return mav("order.html");
	}

	@RequestMapping(value = "/site/report")
	public ModelAndView sale() {
		return mav("reports.html");
	}

	@RequestMapping(value = "/site/login")
	public ModelAndView login() {
		return mav("login.html");
	}

	@RequestMapping(value = "/ui/report/brandReport")
	public ModelAndView brandReport() {
		return mav("brandReport.html");
	}

	@RequestMapping(value = "/ui/report/inventoryReport")
	public ModelAndView inventoryReport() {
		return mav("inventoryReport.html");
	}

	@RequestMapping(value = "/ui/report/daySalesReport")
	public ModelAndView daySalesReport() {
		return mav("daySalesReport.html");
	}

	@RequestMapping(value = "/ui/report/salesReport")
	public ModelAndView salesReport() {
		return mav("salesReport.html");
	}
}
