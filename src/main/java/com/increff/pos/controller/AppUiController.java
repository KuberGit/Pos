package com.increff.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppUiController extends AbstractUiController {

	@RequestMapping(value = "/ui/home")
	public ModelAndView home() {
		return mav("home.html");
	}

	@RequestMapping(value = "/ui/brand")
	public ModelAndView brand() {
		return mav("brand.html");
	}

	@RequestMapping(value = "/ui/product")
	public ModelAndView product() {
		return mav("product.html");
	}

	@RequestMapping(value = "/ui/inventory")
	public ModelAndView inventory() {
		return mav("inventory.html");
	}

	@RequestMapping(value = "/ui/order")
	public ModelAndView orders() {
		return mav("order.html");
	}

	@RequestMapping(value = "/ui/report")
	public ModelAndView reports() {
		return mav("reports.html");
	}

	@RequestMapping(value = "/ui/report/inventoryReport")
	public ModelAndView inventoryReport() {
		return mav("inventoryReport.html");
	}

	@RequestMapping(value = "/ui/report/brandReport")
	public ModelAndView brandsReport() {
		return mav("brandReport.html");
	}

	@RequestMapping(value = "/ui/report/salesReport")
	public ModelAndView salesReport() {
		return mav("salesReport.html");
	}

	@RequestMapping(value = "/ui/report/daySalesReport")
	public ModelAndView dailyReport() {
		return mav("daySalesReport.html");
	}

	@RequestMapping(value = "/ui/admin")
	public ModelAndView admin() {
		return mav("user.html");
	}

}