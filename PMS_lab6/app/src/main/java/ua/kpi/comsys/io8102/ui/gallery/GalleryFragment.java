package ua.kpi.comsys.io8102.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;

import ua.kpi.comsys.io8102.R;

public class GalleryFragment extends Fragment {
    View root;
    private GalleryAdapter adapter;
    ListView listView;
    String API_KEY = "19193969-87191e5db266905fe8936d565";
    int COUNT = 18;
    String REQUEST = "\"small+animals\"";
    String imageUrlTarget="\"webformatURL\":\"";
    URL url;
    LinearLayout layout;
    View elemSet;
    int width;
    int height;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        elemSet = inflater.inflate(R.layout.image_collection, container, false);

        Display screensize = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screensize.getSize(size);
        width = size.x;
        height = size.y;


        listView = root.findViewById(R.id.imagesList);

        try {
            url = new URL("https://pixabay.com/api/?key="+API_KEY+"&q="+REQUEST+"&image_type=photo&per_page="+COUNT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new GetUrlsFromJson("LoadImage").start();

        return root;
    }

    class GalleryAdapter extends ArrayAdapter<List<String>> {
        private final List<List<String>> taskImg;
        Activity generalAct;

        GalleryAdapter(Context context, int textViewResourceId, List<List<String>> objects, Activity generalAct) {
            super(context, textViewResourceId, objects);
            this.taskImg = objects;
            this.generalAct = generalAct;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = inflater.inflate(R.layout.image_collection, parent, false);

            layout = row.findViewById(R.id.gallery_layout);
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = (int)(width*0.8);
            params.width = width;
            layout.setLayoutParams(params);

            List<ImageView> imageViews = new ArrayList<>();
            imageViews.add(row.findViewById(R.id.gal_img1));
            imageViews.add(row.findViewById(R.id.gal_img2));
            imageViews.add(row.findViewById(R.id.gal_img3));
            imageViews.add(row.findViewById(R.id.gal_img4));
            imageViews.add(row.findViewById(R.id.gal_img5));
            imageViews.add(row.findViewById(R.id.gal_img6));

            List<ProgressBar> progressBars = new ArrayList<>();
            progressBars.add(row.findViewById(R.id.load1));
            progressBars.add(row.findViewById(R.id.load2));
            progressBars.add(row.findViewById(R.id.load3));
            progressBars.add(row.findViewById(R.id.load4));
            progressBars.add(row.findViewById(R.id.load5));
            progressBars.add(row.findViewById(R.id.load6));

            int imgNumber = taskImg.get(position).size();

            for (int i=0; i<6; i++){
                try {
                    if (i<imgNumber){
                        try {
                            new DownloadImageTask(imageViews.get(i)).execute(taskImg.get(position).get(i));
                        } catch (Exception e){}
                    }
                    else progressBars.get(i).setVisibility(View.INVISIBLE);
                } catch (Exception ignored){}
            }

            return row;
        }
    }

    class GetUrlsFromJson extends Thread {
        GetUrlsFromJson(String name){
            super(name);
        }

        public void run(){
            List<String> urls = new ArrayList<>();
            List<List<String>> urls_final = new ArrayList<>();

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                String json = "";
                while (true) {
                    if ((inputLine = br.readLine()) == null) break;
                    json += inputLine;
                }

                String S[] = json.split(imageUrlTarget);
                for (String str : S){
                    if (str.substring(0, 4).equals("http")){
                        urls.add(str.split("\",\"")[0]);
                    }
                }

                for (String currentUrl : urls){
                    if (urls_final != null){
                        if (urls_final.size()==0){
                            List<String> tempImageList = new ArrayList<>();
                            urls_final.add(tempImageList);
                        }
                        if (urls_final.get(urls_final.size()-1).size()>=6){
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentUrl);
                            urls_final.add(tempImageList);
                        }
                        else {
                            urls_final.get(urls_final.size()-1).add(currentUrl);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            getActivity().runOnUiThread(new Runnable() { // перенос установки адаптера в основной поток
                @Override
                public void run() {
                    if(urls != null){
                        adapter = new GalleryAdapter(getActivity(), R.layout.image_collection, urls_final, getActivity());
                        listView.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(getContext(), "Failed data load", Toast.LENGTH_LONG).show();
                    }
                }
            });

            System.out.println("URLS: " + urls_final.toString());
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
            int min_len = Math.min(result.getHeight(), result.getWidth());
            if (result == null)
                bmImage.setImageResource(R.drawable.ic_gallery);
//            else bmImage.setImageBitmap(Bitmap.createBitmap(result, 0,0,min_len, min_len)); // crop image(1:1)
            else bmImage.setImageBitmap(result); // set raw image without cropping
        }
    }
}
