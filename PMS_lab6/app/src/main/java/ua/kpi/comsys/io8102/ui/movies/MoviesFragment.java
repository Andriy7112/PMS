package ua.kpi.comsys.io8102.ui.movies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import android.text.method.MovementMethod;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.io8102.R;
import ua.kpi.comsys.io8102.ui.movies.Movie;
import ua.kpi.comsys.io8102.ui.movies.MovieList;


public class MoviesFragment extends Fragment {

    MovieList moviesList;
    ListView moviesListView;
    AdapterMoviesList adapterMoviesList;
    SearchView searchView;
    ArrayList<Movie> movies = new ArrayList<>();
    TextView noResults;
    String API_KEY = "54936890";
    String REQUEST_FILM_NAME;
    Context mainContext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        noResults = view.findViewById(R.id.textView_noResults);
        searchView =  view.findViewById(R.id.search_view);
        moviesListView = (ListView) view.findViewById(R.id.moviesListView);
        adapterMoviesList = new AdapterMoviesList(this.getContext(), movies);
        moviesListView.setAdapter(adapterMoviesList);

        mainContext = this.getContext();

        // searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        registerForContextMenu(moviesListView);

        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                  @Override
                                                  public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                      Movie item_tmp = adapterMoviesList.movies.get(position);

                                                      Ion.with(getContext()).load("http://www.omdbapi.com/?apikey="+API_KEY+"&i="+item_tmp.getImdbID()).asString().setCallback(new FutureCallback<String>() {
                                                          @Override
                                                          public void onCompleted(Exception e, String result) {
                                                              System.out.println(result);
                                                              Movie item = parseMovieFromString(result);

                                                              startActivity(new Intent(view.getContext(), MovieDetailsActivity.class)
                                                                      .putExtra("poster", item.getPoster())
                                                                      .putExtra("title", item.getTitle())
                                                                      .putExtra("year", item.getYear())
                                                                      .putExtra("genre", item.getGenre())
                                                                      .putExtra("director", item.getDirector())
                                                                      .putExtra("writer", item.getWriter())
                                                                      .putExtra("actors", item.getActors())
                                                                      .putExtra("country", item.getCountry())
                                                                      .putExtra("language", item.getLanguage())
                                                                      .putExtra("production", item.getProduction())
                                                                      .putExtra("released", item.getReleased())
                                                                      .putExtra("runtime", item.getRuntime())
                                                                      .putExtra("awards", item.getAwards())
                                                                      .putExtra("rating", item.getImdbRating())
                                                                      .putExtra("votes", item.getImdbVotes())
                                                                      .putExtra("rated", item.getRated())
                                                                      .putExtra("plot", item.getPlot())
                                                              );
                                                          }
                                                      });
                                                  }
                                              }
        );




        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {
                if (movies!=null)
                    movies.clear();

                if(!s.equals("") & s.length()>=3) {
                    try {
                        REQUEST_FILM_NAME = s;

                        Ion.with(getContext()).load("http://www.omdbapi.com/?apikey="+API_KEY+"&s="+REQUEST_FILM_NAME+"&page=1").asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                System.out.println(result);
                                parseFromString(result);
                                for (Movie obj : moviesList.getMovies()){
                                    System.out.println(obj.toString());
                                }
                                movies.addAll(moviesList.getMovies());
                                adapterMoviesList.update();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else {
                    Toast.makeText(getContext(), "Uncorrected request", Toast.LENGTH_LONG).show();
                };


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

//                ArrayList<Movie> results = new ArrayList<>();

//                for (Movie movie: movies) {
//                    if (movie.getTitle().toLowerCase().contains(s.toLowerCase()))
//                        results.add(movie);
//                }

//                adapterMoviesList.update(results);
//
//                if (results.size() == 0) {
//                    moviesListView.setVisibility(View.GONE);
//                    noResults.setVisibility(View.VISIBLE);
//                } else {
//                    noResults.setVisibility(View.GONE);
//                    moviesListView.setVisibility(View.VISIBLE);
//                }

                return true;
            }
        });
    }


    //Parses JSON file with primary film characteristics.
    private void parseFromJson(String fileName) {
        Gson gson = new Gson();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(getFileLocation(fileName)));
            moviesList = gson.fromJson(br, MovieList.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseFromString(String text) {
        Gson gson = new Gson();
        try {
            moviesList = gson.fromJson(text, MovieList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Movie parseMovieFromString(String jsonText) {
        Gson gson = new Gson();

        try {
            Movie parsedMovie = gson.fromJson(jsonText, Movie.class);
            Movie movie = new Movie("Title");

            movie.setTitle(parsedMovie.getTitle());
            movie.setYear(parsedMovie.getYear());
            movie.setGenre(parsedMovie.getGenre());
            movie.setPoster(parsedMovie.getPoster());
            movie.setDirector(parsedMovie.getDirector());
            movie.setWriter(parsedMovie.getWriter());
            movie.setActors(parsedMovie.getActors());
            movie.setCountry(parsedMovie.getCountry());
            movie.setLanguage(parsedMovie.getLanguage());
            movie.setProduction(parsedMovie.getProduction());
            movie.setReleased(parsedMovie.getReleased());
            movie.setRuntime(parsedMovie.getRuntime());
            movie.setAwards(parsedMovie.getAwards());
            movie.setImdbRating(parsedMovie.getImdbRating());
            movie.setImdbVotes(parsedMovie.getImdbVotes());
            movie.setRated(parsedMovie.getRated());
            movie.setPlot(parsedMovie.getPlot());

            return movie;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Parses TXT file with additional film characteristics.
    private void parseFromTxt(String fileName, Movie movie) {
        Gson gson = new Gson();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(getFileLocation(fileName)));
            Movie parsedMovie = gson.fromJson(br, Movie.class);

            movie.setTitle(parsedMovie.getTitle());
            movie.setYear(parsedMovie.getYear());
            movie.setGenre(parsedMovie.getGenre());
            movie.setDirector(parsedMovie.getDirector());
            movie.setWriter(parsedMovie.getWriter());
            movie.setActors(parsedMovie.getActors());
            movie.setCountry(parsedMovie.getCountry());
            movie.setLanguage(parsedMovie.getLanguage());
            movie.setProduction(parsedMovie.getProduction());
            movie.setReleased(parsedMovie.getReleased());
            movie.setRuntime(parsedMovie.getRuntime());
            movie.setAwards(parsedMovie.getAwards());
            movie.setImdbRating(parsedMovie.getImdbRating());
            movie.setImdbVotes(parsedMovie.getImdbVotes());
            movie.setRated(parsedMovie.getRated());
            movie.setPlot(parsedMovie.getPlot());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InputStream getFileLocation(String fileName) {
        return getResources().openRawResource(getResources().getIdentifier(fileName,
                "raw", this.getContext().getPackageName()));
    }
}