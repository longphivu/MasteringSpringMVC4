package masterSpringMvc.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import masterSpringMvc.date.USLocalDateFormatter;

@Controller
public class ProfileController {
	
	@ModelAttribute("dateFormat")
	public String localeFormat(Locale locale) {
		return USLocalDateFormatter.getPattern(locale);
	}
	
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
	public String displayProfile(ProfileForm profileForm) {
		return "/profile/profilePage";
	}

	@RequestMapping(value = "/profile", params = { "save" }, method = RequestMethod.POST)
	public String saveProfile(@Valid ProfileForm profileForm, BindingResult binding) {
		
		if (binding.hasErrors()) {
			return "/profile/profilePage";
		}
		
		System.out.println("ok " + profileForm);
		return "redirect:/profile";
	}
	
	@RequestMapping(value = "/profile", params = { "addTaste" })
	public String addRow(ProfileForm profileForm, BindingResult binding) {
		profileForm.getTastes().add(null);
		return "profile/profilePage";
	}
	
	@RequestMapping(value = "/profile", params = { "removeTaste" })
	public String removeRow(ProfileForm profileForm, HttpServletRequest req) {
		Integer rowId = Integer.valueOf(req.getParameter("removeTaste"));
		profileForm.getTastes().remove(rowId.intValue());
		return "profile/profilePage";
	}

}
