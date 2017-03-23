package masterSpringMvc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TwitterController {

	@Autowired
	private Twitter twitter;
	
	@RequestMapping(value = "/")
	public String home() {
		return "searchPage";
	}
	
	@RequestMapping(value = "/result")
	public String getTweets(@RequestParam(defaultValue = "masterSpringMVC4") String search, Model model) {
		SearchResults results = twitter.searchOperations().search(search);
		//List<String> tweets = results.getTweets().stream().map((Tweet t) -> t.getText()).collect(Collectors.toList());
		List<Tweet> tweets = results.getTweets();
		model.addAttribute("tweets", tweets);
		model.addAttribute("search", search);
		return "resultPage";
	}
	
	@RequestMapping(value = "/postSearch", method = RequestMethod.POST)
	public String postSearch(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		String search = request.getParameter("search");
		if (search.toLowerCase().contains("struts")) {
			redirectAttributes.addFlashAttribute("error", "Try using spring instead!");
			return "redirect:/";
		}
		redirectAttributes.addAttribute("search", search);
		return "forward:result";
	}
	

}
