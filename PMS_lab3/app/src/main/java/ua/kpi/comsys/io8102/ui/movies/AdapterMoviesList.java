package ua.kpi.comsys.io8102.ui.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.kpi.comsys.io8102.R;


public class AdapterMoviesList extends ArrayAdapter<String> {

    ArrayList<String> titles;
    ArrayList<String> years;
    ArrayList<String> imdbIDs;
    ArrayList<String> types;
    ArrayList<String> posters;

    Context mContext;

    public AdapterMoviesList(Context context, ArrayList<String> titles, ArrayList<String> years,
                             ArrayList<String> imdbIDs, ArrayList<String> types,
                             ArrayList<String> posters) {

        super(context, R.layout.listview_item);
        this.titles = titles;
        this.years = years;
        this.imdbIDs = imdbIDs;
        this.types = types;
        this.posters = posters;

        this.mContext = context;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

            mViewHolder.mTitle.setText(titles.get(position));
            mViewHolder.mYear.setText(years.get(position));
            mViewHolder.mImdbID.setText(imdbIDs.get(position));
            mViewHolder.mType.setText(types.get(position));

            mViewHolder.mPoster.setImageResource(mContext.getResources().getIdentifier(
                    posters.get(position), "drawable", mContext.getPackageName()));

        return convertView;
    }

    private static class ViewHolder {
        TextView mTitle;
        TextView mYear;
        TextView mImdbID;
        TextView mType;
        ImageView mPoster;
    }
}