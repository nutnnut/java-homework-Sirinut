package com.wongnai.interview.movie.search;

import java.util.*;

import com.wongnai.interview.movie.*;
import com.wongnai.interview.movie.sync.MovieDataSynchronizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;

@Component("invertedIndexMovieSearchService")
@DependsOn("movieDatabaseInitializer")
public class InvertedIndexMovieSearchService implements MovieSearchService {
	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private InvertedIndexRepository invertedIndexRepository;

	@Override
	public List<Movie> search(String queryText) {
		//TODO: Step 4 => Please implement in-memory inverted index to search movie by keyword.
		// You must find a way to build inverted index before you do an actual search.
		// Inverted index would looks like this:
		// -------------------------------
		// |  Term      | Movie Ids      |
		// -------------------------------
		// |  Star      |  5, 8, 1       |
		// |  War       |  5, 2          |
		// |  Trek      |  1, 8          |
		// -------------------------------
		// When you search with keyword "Star", you will know immediately, by looking at Term column, and see that
		// there are 3 movie ids contains this word -- 1,5,8. Then, you can use these ids to find full movie object from repository.
		// Another case is when you find with keyword "Star War", there are 2 terms, Star and War, then you lookup
		// from inverted index for Star and for War so that you get movie ids 1,5,8 for Star and 2,5 for War. The result that
		// you have to return can be union or intersection of those 2 sets of ids.
		// By the way, in this assignment, you must use intersection so that it left for just movie id 5.

		String[] words = queryText.split(" ");
		//Empty Query
		if(words.length==0) return new ArrayList<Movie>();

		Set<Long> index = new HashSet<>();
		try{
			//System.out.println(words[0].toUpperCase());
			index.addAll(invertedIndexRepository.findByWord(words[0].toUpperCase()).get(0).getIndex());
			//System.out.println(index);
			for (int i = 1; i < words.length; i++){
				index.retainAll(invertedIndexRepository.findByWord(words[i].toUpperCase()).get(0).getIndex());
				//System.out.println(index);
			}

			if(index.isEmpty()) return new ArrayList<Movie>();
			return movieRepository.findByIndex(index);

		}catch (IndexOutOfBoundsException e){
			// one of the keyword is not found in the inverted index, therefore there is no match
			return new ArrayList<Movie>();
		}
	}
}
