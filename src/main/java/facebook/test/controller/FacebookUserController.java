package facebook.test.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import facebook.test.dao.FacebookUserDao;
import facebook.test.model.FacebookUser;

@Controller
public class FacebookUserController {

	@Autowired
	FacebookUserDao dao;

	@RequestMapping("/registerform")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new FacebookUser());
		return "index";
	}

	@RequestMapping(value = "/registerdata", method = RequestMethod.POST)
	public String registerFBUser(@ModelAttribute("user") FacebookUser user, HttpSession session) {
		dao.saveUserData(user);
		session.setAttribute("loggedInUser", user);
		return "redirect:/login";
	}

	@RequestMapping("/login")
	public String showLoginForm(Model model, HttpSession session) {
		model.addAttribute("user", new FacebookUser());
		
		String logoutMessage = (String) session.getAttribute("logoutMessage");
		if (logoutMessage != null) {
			model.addAttribute("logoutMessage", logoutMessage);
			// Remove the flash attribute from session
			session.removeAttribute("logoutMessage");
		}
		return "loginForm";
	}

	@RequestMapping(value = "/savelogindata", method = RequestMethod.POST)
	public String loginUser(@ModelAttribute("user") FacebookUser user, HttpSession session) {
		// Call the loginUser method from the DAO to authenticate the user
		FacebookUser loggedInUser = dao.loginUser(user.getFbName(), user.getFbPassword());
		if (loggedInUser != null) {
			// Set the loggedInUser attribute in the session
			session.setAttribute("loggedInUser", loggedInUser);
			return "redirect:/dashboard";
		} else {
			// Redirect back to the login page with an error message if login fails
			return "redirect:/login?error";
		}
	}

	@RequestMapping("/dashboard")
	public String showDashboard(Model model, HttpSession session) {
		FacebookUser loggedInUser = (FacebookUser) session.getAttribute("loggedInUser");

		if (loggedInUser != null) {
			model.addAttribute("loggedInUser", loggedInUser);
			return "dashboardData";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping("/logout")
	public String logoutUser(HttpSession session, RedirectAttributes redirectAttributes) {
		// Invalidate the session
		session.invalidate();
		// Add a flash attribute with the success message
		redirectAttributes.addFlashAttribute("logoutMessage", "User logout successfully");
		return "redirect:/login";
	}

	@RequestMapping("/forgotpassword")
	public String forgotExistingPassword(Model model) {
		model.addAttribute("user", new FacebookUser());
		return "newPassword";
	}

	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public String updateFacebookPassword(@ModelAttribute("user") FacebookUser user, HttpSession session) {
		String fbName = user.getFbName();
		String newPassword = user.getFbPassword();

		int rowsAffected = dao.updatePassword(fbName, newPassword);
		if (rowsAffected > 0) {
			return "redirect:/login";
		} else {
			return "newPassword";
		}
	}
}
