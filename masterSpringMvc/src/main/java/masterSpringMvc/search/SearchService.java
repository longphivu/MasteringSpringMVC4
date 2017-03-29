package masterSpringMvc.search;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
	private Twitter twitter;

	@Autowired
	public SearchService(Twitter twitter) {
		this.twitter = twitter;
	}

	public List<Tweet> search(String searchType, List<String> keywords) {
		
		List<SearchParameters> searchs = keywords.stream()
				.map(taste -> createSearchParam(searchType, taste))
				.collect(Collectors.toList());
		
		List<Tweet> tweets = searchs.stream()
				.map(param -> twitter.searchOperations().search(param))
				.flatMap(searchResults -> searchResults.getTweets().stream())
				.collect(Collectors.toList());
		
		return tweets;
	}
	
	private SearchParameters createSearchParam(String searchType, String taste) {
		SearchParameters.ResultType resultType = getResultType(searchType);
		SearchParameters searchParameters = new SearchParameters(taste);
		searchParameters.resultType(resultType);
		searchParameters.count(3);
		return searchParameters;
	}
	
	private SearchParameters.ResultType getResultType(String searchType) {
		for (SearchParameters.ResultType r : SearchParameters.ResultType.values()) {
			if (r.toString().equals(searchType)) {
				return r;
			}
		}
		return SearchParameters.ResultType.RECENT;
	}
}
