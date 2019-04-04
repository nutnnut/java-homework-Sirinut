package com.wongnai.interview.movie.external;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class MovieDataServiceImpl implements MovieDataService {
	public static final String MOVIE_DATA_URL
			= "https://raw.githubusercontent.com/prust/wikipedia-movie-data/master/movies.json";

	@Autowired
	private RestOperations restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	// https://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
	// Roland Illig
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static JSONArray readJSONfromURL(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	@Override
	public MoviesResponse fetchAll() {
		//TODO:
		// Step 1 => Implement this method to download data from MOVIE_DATA_URL and fix any error you may found.
		// Please noted that you must only read data remotely and only from given source,
		// do not download and use local file or put the file anywhere else.
		JSONArray jsonArray;
		MoviesResponse response = null;
		try {
			jsonArray = readJSONfromURL(MOVIE_DATA_URL);
			response = getMoviesResponseFromJSON(jsonArray);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return response;
	}

	public MoviesResponse getMoviesResponseFromJSON(JSONArray jsonArray) throws JSONException {
		MoviesResponse response = new MoviesResponse();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject movie_i = jsonArray.getJSONObject(i);

			String title = movie_i.getString("title");

			List<String> cast = new ArrayList<>();
			JSONArray movie_i_cast = movie_i.getJSONArray("cast");
			if (cast != null) {
				for (int j=0; j<movie_i_cast.length(); j++){
					cast.add(movie_i_cast.get(j).toString());
				}
			}

			List<String> genres = new ArrayList<>();
			JSONArray movie_i_genres = movie_i.getJSONArray("genres");
			if (genres != null) {
				for (int j=0; j<movie_i_genres.length(); j++){
					genres.add(movie_i_genres.get(j).toString());
				}
			}

			int year = movie_i.getInt("year");

			MovieData movie = new MovieData();

			movie.setTitle(title);
			movie.setCast(cast);
			movie.setGenres(genres);
			movie.setYear(year);

			response.add(movie);

		}

		return response;
	}
}
