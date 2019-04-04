package com.wongnai.interview.movie.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wongnai.interview.movie.external.MovieData;
import com.wongnai.interview.movie.external.MoviesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wongnai.interview.movie.Movie;
import com.wongnai.interview.movie.MovieSearchService;
import com.wongnai.interview.movie.external.MovieDataService;

@Component("simpleMovieSearchService")
public class SimpleMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieDataService movieDataService;

	// https://stackoverflow.com/questions/25417363/java-string-contains-matches-exact-word
	// Jaskey
	private static boolean isContain(String source, String subItem){
		source = source.toLowerCase();
		subItem = subItem.toLowerCase();
		String pattern = "\\b"+subItem+"\\b";
		Pattern p= Pattern.compile(pattern);
		Matcher m=p.matcher(source);
		return m.find();
	}

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 2 => Implement this method by using data from MovieDataService
		// All test in SimpleMovieSearchServiceIntegrationTest must pass.
		// Please do not change @Component annotation on this class

		List<Movie> response = new ArrayList<>();

		// Skip search entirely if query contains space(multi-word)
		if(!queryText.contains(" ")) {
			MoviesResponse result = movieDataService.fetchAll();
			result.removeIf(n -> (!isContain(n.getTitle(), queryText)));

			for(MovieData i : result){
				Movie movie = new Movie(i.getTitle());
				List<String> actors = new ArrayList<>(i.getCast());
				movie.setActors(actors);
				response.add(movie);
			}
		}

		return response;
	}
}
