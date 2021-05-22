package ua.kpi.comsys.io8102.ui.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.io8102.R;
import ua.kpi.comsys.io8102.database.App;
import ua.kpi.comsys.io8102.database.AppDatabase;
import ua.kpi.comsys.io8102.database.MovieDao;
import ua.kpi.comsys.io8102.database.MovieEntities;
import ua.kpi.comsys.io8102.database.PosterDao;
import ua.kpi.comsys.io8102.database.PosterEntities;


public class MoviesFragment extends Fragment {

    MovieList moviesList;
    ListView moviesListView;
    AdapterMoviesList adapterMoviesList;
    SearchView searchView;
    List<Movie> movies = new ArrayList<>();
    TextView noResults;
    String API_KEY = "54936890";
    String REQUEST_MOVIE_NAME;
    Context mainContext;
    static AppDatabase db = App.getInstance().getDatabase();
    MovieDao movieDao = db.movieDao();
    static PosterDao posterDao = db.posterDao();
    static String FILE_USER_NAME = "movie_json.txt";
    static Activity currentUI;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        currentUI = getActivity();
        noResults = view.findViewById(R.id.textView_noResults);
        searchView =  view.findViewById(R.id.search_view);
        moviesListView = (ListView) view.findViewById(R.id.moviesListView);
        adapterMoviesList = new AdapterMoviesList(this.getContext(), movies);
        moviesListView.setAdapter(adapterMoviesList);

        mainContext = this.getContext();

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
                        REQUEST_MOVIE_NAME = s;
                        REQUEST_MOVIE_NAME = REQUEST_MOVIE_NAME.replace(" ", "+");

                        while (REQUEST_MOVIE_NAME.startsWith("+")) REQUEST_MOVIE_NAME = REQUEST_MOVIE_NAME.substring(1); // вырезаем пробелы(+) из начала строки
                        while (REQUEST_MOVIE_NAME.endsWith("+")) REQUEST_MOVIE_NAME = REQUEST_MOVIE_NAME.substring(0, REQUEST_MOVIE_NAME.length()-2); // вырезаем пробелы(+) из конца строки

                        new SearchHandler("handle").start();

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

    class SaveMoviesToDB extends Thread {
        SaveMoviesToDB(String name){
            super(name);
        }

        public void run(){
            try {
                if (movieDao.getByRequest(REQUEST_MOVIE_NAME).size() == 0) {
                    MovieEntities movieEntity;
                    if (movies != null)
                        for (Movie currentMovie : movies) {
                            movieEntity = new MovieEntities();
                            movieEntity.Title = currentMovie.getTitle();
                            movieEntity.Year = currentMovie.getYear();
                            movieEntity.Type = currentMovie.getType();
                            movieEntity.Poster = currentMovie.getPoster();
                            movieEntity.imdbID = currentMovie.getImdbID();
                            movieEntity.SearchRequest = REQUEST_MOVIE_NAME;
                            movieDao.insert(movieEntity);
                        }
                }
            } catch (Exception e){}
        }
    }

    class SearchHandler extends Thread {
        SearchHandler(String name){
            super(name);
        }

        public void run(){
            List<MovieEntities> entityByRequest = movieDao.getByRequest(REQUEST_MOVIE_NAME);
            String requestUrl = "http://www.omdbapi.com/?apikey="+API_KEY+"&s="+ REQUEST_MOVIE_NAME +"&page=1";
            try {
                if (!netIsAvailable() & entityByRequest.size() > 0) {
                    movies = Movie.listMovieEntitiesToListMovies(entityByRequest);
                    exportToJSON(getContext(), movies);
                    try {
                        if (movies == null)
                            movies = new ArrayList<>();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AdapterMoviesList adapter3 = new AdapterMoviesList(getContext(), movies);
                                moviesListView.setAdapter(adapter3);
                            }
                        });

                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } else {
                    Ion.with(getContext()).load(requestUrl).asString().setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if(result==null){
                                Toast.makeText(getContext(), "There is no internet connection, movies were not found in the database.", Toast.LENGTH_LONG).show();
                                movies = new ArrayList<>();
                            }
                            else {
                                parseFromString(result);
                                movies = moviesList.getMovies();
                            }

                            exportToJSON(getContext(), movies);

                            new SaveMoviesToDB("SaveMovies").start();
                            try {
                                if (movies==null){
                                    movies = new ArrayList<>();
                                    Toast.makeText(getContext(), "Nothing found", Toast.LENGTH_LONG).show();
                                }
                                AdapterMoviesList adapter3 = new AdapterMoviesList(getActivity(), movies);
                                moviesListView.setAdapter(adapter3);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class PosterHandler implements Runnable {
        protected ImageView imageView;
        protected String posterUrl;
        protected Context context;
        protected int position;

        public PosterHandler(ImageView imageView, String posterUrl, int position, Context context) {
            this.imageView = imageView;
            this.posterUrl = posterUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            PosterEntities currentImage = new PosterEntities();
            System.out.println("Pos:"+position+"; URL:"+posterUrl);
            String fileName;

            if (posterUrl.startsWith("http")) {
                List<PosterEntities> daoByUrl = posterDao.getByUrl(posterUrl);
                String cacheDir = context.getCacheDir() + "";

                boolean imageExist = false;
                if (daoByUrl.size() != 0) {
                    String imageCachePath = cacheDir + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists();
                }

                if (daoByUrl.size() == 0 | !imageExist) {
                    if (!imageExist & daoByUrl.size()>0)
                        fileName = daoByUrl.get(0).getFileName();
                    else {
                        int rndInt = new Random().nextInt(9999);
                        fileName = "poster_" + position+ "_" + rndInt+".png";
                        while (true){
                            if (!(new File(cacheDir + "/" + fileName).exists())) break;
                            System.out.println("WHILE lifecycle(");
                            fileName = "poster_" + position+ "_" + new Random().nextInt(9999)+".png";
                        }
                    }

                    URL urlDownload;
                    try {
                        urlDownload = new URL(posterUrl);
                        InputStream input = urlDownload.openStream();
                        try {
                            OutputStream output = new FileOutputStream(cacheDir + "/" + fileName);
                            try {
                                byte[] buffer = new byte[2048];
                                int bytesRead = 0;
                                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                                    output.write(buffer, 0, bytesRead);
                                }
                            } finally {
                                output.close();
                            }
                        } finally {
                            input.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    currentImage.url = posterUrl;
                    currentImage.fileName = fileName;
                    posterDao.insert(currentImage);
                }

                try {
                    String imageNameDB = posterDao.getByUrl(posterUrl).get(0).getFileName();

                    File imageFile = new File(context.getCacheDir() + "/" + imageNameDB);
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is);

                    currentUI.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(userImage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (imageView != null) {
                currentUI.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageResource(R.drawable.ic_gallery);
                    }
                });
            }
        }
    }

    private static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean exportToJSON(Context context, List<Movie> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setSearch(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(FILE_USER_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static class DataItems {
        private List<Movie> Search;

        List<Movie> getSearch() {
            return Search;
        }
        void setSearch(List<Movie> search) {
            this.Search = search;
        }
    }
}