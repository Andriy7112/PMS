package ua.kpi.comsys.io8102.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ua.kpi.comsys.io8102.R;
import ua.kpi.comsys.io8102.database.App;
import ua.kpi.comsys.io8102.database.AppDatabase;
import ua.kpi.comsys.io8102.database.ImageDao;
import ua.kpi.comsys.io8102.database.ImageEntities;

public class GalleryFragment extends Fragment {
    View root;
    private GalleryAdapter adapter;
    ListView listView;
    String API_KEY = "19193969-87191e5db266905fe8936d565";
    int COUNT = 18;
    String REQUEST = "\"small+animals\"";
    String imageUrlTarget="\"previewURL\":\"";
    URL url;
    LinearLayout layout;
    View elemSet;
    int width;
    int height;
    List<List<String>> urls_final = new ArrayList<>();
    static AppDatabase db = App.getInstance().getDatabase();
    static ImageDao imageDao = db.imageDao();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        elemSet = inflater.inflate(R.layout.image_collection, container, false);

        Display screensize = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screensize.getSize(size);
        width = size.x;
        height = size.y;

        try {
            url = new URL("https://pixabay.com/api/?key="+API_KEY+"&q="+REQUEST+"&image_type=photo&per_page="+COUNT);
            new GetUrlsFromJson("load").start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
                        ImageHandlerImg handler = new ImageHandlerImg(imageViews.get(i), getActivity(), taskImg.get(position).get(i), position, getContext());
                        Thread thread = new Thread(handler);
                        thread.start();
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
            if (netIsAvailable()) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    String inputLine;
                    String json = "";
                    while (true) {
                        if ((inputLine = br.readLine()) == null) break;
                        json += inputLine;
                    }

                    List<String> urls = new ArrayList<>();

                    String S[] = json.split(imageUrlTarget);
                    for (String str : S) {
                        if (str.substring(0, 4).equals("http")) {
                            urls.add(str.split("\",\"")[0]);
                        }
                    }
                    try {
                        urls_final.clear();
                    } catch (Exception e) {}

                    for (String currentUrl : urls) {
                        if (urls_final != null) {
                            if (urls_final.size() == 0) {
                                List<String> tempImageList = new ArrayList<>();
                                urls_final.add(tempImageList);
                            }
                            if (urls_final.get(urls_final.size() - 1).size() >= 6) {
                                List<String> tempImageList = new ArrayList<>();
                                tempImageList.add(currentUrl);
                                urls_final.add(tempImageList);
                            } else {
                                urls_final.get(urls_final.size() - 1).add(currentUrl);
                            }
                        }
                    }

                    br.close();

                    listView = root.findViewById(R.id.imagesList);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (urls_final != null) {
                                adapter = new GalleryAdapter(getActivity(), R.layout.image_collection, urls_final, getActivity());

                                listView.setAdapter(adapter);
                            } else {
                                Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
//                e.printStackTrace();
                }
            }

            else {
                System.out.println("ENTER WITHOUT CONNECTION");
                List<ImageEntities> imageEntities = imageDao.getAll();
                System.out.println("NUM OF ENTITY: "+imageEntities.size());

                for (ImageEntities currentEntity : imageEntities) {
                    if (urls_final != null) {
                        if (urls_final.size() == 0) {
                            List<String> tempImageList = new ArrayList<>();
                            urls_final.add(tempImageList);
                        }
                        if (urls_final.get(urls_final.size() - 1).size() >= 6) {
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(currentEntity.getUrl());
                            urls_final.add(tempImageList);
                        } else {
                            urls_final.get(urls_final.size() - 1).add(currentEntity.getUrl());
                        }
                    }
                }

                System.out.println("NI//SIZE: "+urls_final.size()+"; IMAGES: "+urls_final);



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_LONG).show();

                        if (urls_final != null) {
                            listView = root.findViewById(R.id.imagesList);

                            adapter = new GalleryAdapter(getActivity(), R.layout.image_collection, urls_final, getActivity());

                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Failed to get data", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(urls_final != null){
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

    public static class ImageHandlerImg implements Runnable {
        protected ImageView imageView;
        protected Activity uiActivity;
        protected String imageUrl;
        protected Context context;
        protected int position;

        public ImageHandlerImg(ImageView imageView, Activity uiActivity, String imageUrl, int position, Context context) {
            this.imageView = imageView;
            this.uiActivity = uiActivity;
            this.imageUrl = imageUrl;
            this.position = position;
            this.context = context;
        }

        public void run() {
            ImageEntities currentImage = new ImageEntities();
            System.out.println("Pos:"+position+"; URL:"+ imageUrl);
            String fileName;

            if (imageUrl.startsWith("http")) {
                List<ImageEntities> daoByUrl = imageDao.getByUrl(imageUrl);
                String cacheDir = context.getCacheDir() + "";

                boolean imageExist = false;
                if (daoByUrl.size() != 0) {
                    String imageCachePath = cacheDir + "/" + daoByUrl.get(0).getFileName();
                    imageExist = new File(imageCachePath).exists();
                    System.out.println("FILE:"+daoByUrl.get(0).getFileName()+"; Exist:"+imageExist);
                }

                if (daoByUrl.size() == 0 | !imageExist) {
                    if (!imageExist & daoByUrl.size()>0)
                        fileName = daoByUrl.get(0).getFileName();
                    else {
                        fileName = "image_" + position+ "_" + hashCode() +".png";
                        while (true){
                            if (!(new File(cacheDir + "/" + fileName).exists())) break;
                            System.out.println("WHILE lifecycle(");
                            fileName = "image_" + position+ "_" + new Random().nextInt(500)+".png";
                        }
                    }

                    URL urlDownload;
                    try {
                        urlDownload = new URL(imageUrl);
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

                    currentImage.url = imageUrl;
                    currentImage.fileName = fileName;
                    imageDao.insert(currentImage);
                }

                try {
                    String imageNameDB = imageDao.getByUrl(imageUrl).get(0).getFileName();

                    File imageFile = new File(context.getCacheDir() + "/" + imageNameDB);
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is);

                    uiActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(userImage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (imageView != null) {
                uiActivity.runOnUiThread(new Runnable() {
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
}
