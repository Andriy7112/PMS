package ua.kpi.comsys.io8102.ui.movies;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.kpi.comsys.io8102.R;
import ua.kpi.comsys.io8102.ui.movies.Movie;


public class AdapterMoviesList extends ArrayAdapter<String> {

    ArrayList<Movie> movies;
    Context mContext;


    public AdapterMoviesList(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.listview_item);
        this.movies = movies;
        this.mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder mViewHolder = new ViewHolder();
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) mContext.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.listview_item, parent, false);


                mViewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                mViewHolder.mYear = (TextView) convertView.findViewById(R.id.year);
                mViewHolder.mImdbID = (TextView) convertView.findViewById(R.id.imdbID);
                mViewHolder.mType = (TextView) convertView.findViewById(R.id.type);
                mViewHolder.mPoster = (ImageView) convertView.findViewById(R.id.poster);

                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            mViewHolder.mTitle.setText(movies.get(position).getTitle());
            mViewHolder.mYear.setText(movies.get(position).getYear());
            mViewHolder.mImdbID.setText(movies.get(position).getImdbID());
            mViewHolder.mType.setText(movies.get(position).getType());

            mViewHolder.mPoster.setImageResource(mContext.getResources().getIdentifier(
                    movies.get(position).getPoster(), "drawable", mContext.getPackageName()));

        } catch (Resources.NotFoundException | IndexOutOfBoundsException |
                NumberFormatException e) {

            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    public void update(ArrayList<Movie> results) {
            movies = new ArrayList<>();
            movies.addAll(results);
            notifyDataSetChanged();
    }

    public void update() {
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView mTitle;
        TextView mYear;
        TextView mImdbID;
        TextView mType;
        ImageView mPoster;
    }
}