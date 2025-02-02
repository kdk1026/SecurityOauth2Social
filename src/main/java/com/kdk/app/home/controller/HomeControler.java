package com.kdk.app.home.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 2. 2. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
@Controller
public class HomeControler {

	@GetMapping("/home")
	public ModelAndView home(@AuthenticationPrincipal OAuth2User principal) {
		ModelAndView mav = new ModelAndView();

		mav.addObject("name", principal.getAttribute("name"));
		mav.addObject("email", principal.getAttribute("email"));

		mav.setViewName("home/home");
		return mav;
	}

}
