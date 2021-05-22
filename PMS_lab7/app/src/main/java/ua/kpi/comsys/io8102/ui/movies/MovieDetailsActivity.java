package ua.kpi.comsys.io8102.ui.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import ua.kpi.comsys.io8102.R;


public class MovieDetailsActivity extends AppCompatActivity {

    ImageView poster;
    TextView title;
    TextView year;
    TextView genre;
    TextView director;
    TextView writer;
    TextView actors;
    TextView country;
    TextView language;
    TextView production;
    TextView released;
    TextView runtime;
    TextView awards;
    TextView rating;
    TextView votes;
    TextView rated;
    TextView plot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        poster = (ImageView) findViewById(R.id.imageView_poster);
        title = (TextView) findViewById(R.id.textView_title);
        year = (TextView) findViewById(R.id.textView_year);
        genre = (TextView) findViewById(R.id.textView_genre);
        director = (TextView) findViewById(R.id.textView_director);
        writer = (TextView) findViewById(R.id.textView_writer);
        actors = (TextView) findViewById(R.id.textView_actors);
        country = (TextView) findViewById(R.id.textView_country);
        language = (TextView) findViewById(R.id.textView_language);
        production = (TextView) findViewById(R.id.textView_production);
        released = (TextView) findViewById(R.id.textView_released);
        runtime = (TextView) findViewById(R.id.textView_runtime);
        awards = (TextView) findViewById(R.id.textView_awards);
        rating = (TextView) findViewById(R.id.textView_rating);
        votes = (TextView) findViewById(R.id.textView_votes);
        rated = (TextView) findViewById(R.id.textView_rated);
        plot = (TextView) findViewById(R.id.textView_plot);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!bundle.getString("poster").equals("")) {
//                poster.setImageResource(this.getResources().getIdentifier(
//                        bundle.getString("poster"), "drawable", this.getPackageName()));
                System.out.println("ENTER SET IMAGE");
                try {
                    new DownloadImageTask(poster).execute(bundle.getString("poster"));
                } catch (Exception e){}
            }
            title.setText(bundle.getString("title"));
            year.setText(bundle.getString("year"));
            genre.setText(bundle.getString("genre"));
            director.setText(bundle.getString("director"));
            writer.setText(bundle.getString("writer"));
            actors.setText(bundle.getString("actors"));
            country.setText(bundle.getString("country"));
            language.setText(bundle.getString("language"));
            production.setText(bundle.getString("production"));
            released.setText(bundle.getString("released"));
            runtime.setText(bundle.getString("runtime"));
            awards.setText(bundle.getString("awards"));
            rating.setText(bundle.getString("rating"));
            votes.setText(bundle.getString("votes"));
            rated.setText(bundle.getString("rated"));
            plot.setText(bundle.getString("plot"));
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null)
                bmImage.setImageResource(R.drawable.ic_gallery);
            else bmImage.setImageBitmap(result);
        }
    }
}