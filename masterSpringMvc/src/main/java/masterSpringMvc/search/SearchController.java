package masterSpringMvc.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

	private SearchService searchService;

	@Autowired
	public SearchController(SearchService searchService) {
		super();
		this.searchService = searchService;
	}
	
	@RequestMapping(value = "/search/{searchType}")
	public String search(@PathVariable String searchType, @MatrixVariable List<String> keywords, Model model) {
		List<Tweet> tweets = searchService.search(searchType, keywords);
		model.addAttribute("tweets", tweets);
		model.addAttribute("search", keywords);
		return "resultPage";
	}
}
