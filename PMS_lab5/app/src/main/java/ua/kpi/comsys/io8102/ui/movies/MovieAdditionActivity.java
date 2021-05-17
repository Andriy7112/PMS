package ua.kpi.comsys.io8102.ui.movies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ua.kpi.comsys.io8102.R;


public class MovieAdditionActivity extends AppCompatActivity {

    EditText editText_title;
    EditText editText_year;
    EditText editText_imdbId;
    EditText editText_type;
    Button buttonAdd;

    String title;
    int year;
    String imdbId;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_addition);

        editText_title = (EditText) findViewById(R.id.editText_title);
        editText_year = (EditText) findViewById(R.id.editText_year);
        editText_imdbId = (EditText) findViewById(R.id.editText_imdbId);
        editText_type = (EditText) findViewById(R.id.editText_type);
        buttonAdd = (Button) findViewById(R.id.button_add);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    title = editText_title.getText().toString().trim();
                    year = Integer.parseInt(editText_year.getText().toString().trim());
                    imdbId = editText_imdbId.getText().toString().trim();
                    type = editText_type.getText().toString().trim();

                    Intent intent = new Intent();
                    intent.putExtra("title", title);
                    intent.putExtra("year", year);
                    intent.putExtra("imdbId", imdbId);
                    intent.putExtra("type", type);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(getApplicationContext(),
                            "Invalid format for 'year' parameter. Type integer value.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

}