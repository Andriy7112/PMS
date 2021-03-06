package ua.kpi.comsys.io8102.ui.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import ua.kpi.comsys.io8102.R;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment {
    View root;
    List<List<String>> currentImgs = new ArrayList<>();
    private final int success = 1;
    private GalleryAdapter adapter;
    String elemsSettingsName = "collections";
    ListView listView;
    Boolean fragment_enable = false;

    public String imgsToString(List<List<String>> img){
        StringBuilder result = new StringBuilder();
        for (List<String> obj1 : img)
            for (int i = 0; i < obj1.size(); i++) {
                result.append(obj1.get(i));
                result.append(";");
            }
        String resultStr = result.toString();
        if (resultStr.length()>0)
            return resultStr.substring(0, resultStr.length() - 1);
        return "";
    }

    public List<List<String>> imgsFromString(String imgStr){
        List<List<String>> result = new ArrayList<>();
        List<String> firstStep = new ArrayList<String>(Arrays.asList(imgStr.split(";")));

        if (imgStr.equals(""))
            return result;

        for (String obj : firstStep) {
            if (result.size() == 0) {
                List<String> tempImageList = new ArrayList<>();
                result.add(tempImageList);
            }
            if (result.get(result.size() - 1).size() >= 6) {
                List<String> tempImageList = new ArrayList<>();
                tempImageList.add(obj);
                result.add(tempImageList);
            } else {
                result.get(result.size() - 1).add(obj);
            }
        }
        return result;
    }

    public void setImagesList(){
        if (!fragment_enable) {
            SharedPreferences settings = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            currentImgs = imgsFromString(settings.getString(elemsSettingsName, ""));
            if (currentImgs != null & currentImgs.size() > 0) {
                if (currentImgs.get(0).size() > 0) {
                    adapter = new GalleryAdapter(getActivity(), R.layout.image_collection, currentImgs, getActivity());
                    listView.setAdapter(adapter);
                } else
                    currentImgs = new ArrayList<>();
            } else
                currentImgs = new ArrayList<>();
            fragment_enable = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragment_enable =false;
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences settings = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(elemsSettingsName, imgsToString(currentImgs));
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        setImagesList();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ImageButton addImageButton = root.findViewById(R.id.addImage);
        listView = root.findViewById(R.id.imagesList);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, success);
            }
        });

        return root;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (!fragment_enable){
            fragment_enable = true;
        }

        if (requestCode == success & imageReturnedIntent!=null) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData();
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream);

                    String newImageName = "upload"+imageUri.hashCode()+".jpeg";

                    if (currentImgs != null){
                        if (currentImgs.size()==0){
                            List<String> tempImageList = new ArrayList<>();
                            currentImgs.add(tempImageList);
                        }
                        if (currentImgs.get(currentImgs.size()-1).size()>=6){
                            List<String> tempImageList = new ArrayList<>();
                            tempImageList.add(newImageName);
                            currentImgs.add(tempImageList);
                        }
                        else {
                            currentImgs.get(currentImgs.size()-1).add(newImageName);
                        }
                    }

                    ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                    if (selectedImage1.getWidth() < 500 | selectedImage1.getHeight() < 500)
                        selectedImage1.compress(Bitmap.CompressFormat.JPEG, 90, bos2);
                    else {
                        selectedImage1 = Bitmap.createScaledBitmap(selectedImage1, 400, 400, false);
                        selectedImage1.compress(Bitmap.CompressFormat.JPEG, 60, bos2);
                    }

                    byte[] bitmapdata = bos2.toByteArray();
                    File imageFile = new File(getContext().getFilesDir(), newImageName);

                    try {
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(currentImgs != null & currentImgs.get(0).size()==1){
                        adapter = new GalleryAdapter(getActivity(), R.layout.image_collection, currentImgs, getActivity());
                        listView.setAdapter(adapter);
                    }
                    else if (currentImgs.size()>0){
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class GalleryAdapter extends ArrayAdapter<List<String>> {
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
                        LoadImage handler = new LoadImage(imageViews.get(i), generalAct, position, getContext(), taskImg.get(position).get(i));
                        Thread th = new Thread(handler);
                        th.start();
                    }
                    else progressBars.get(i).setVisibility(View.INVISIBLE);
                } catch (Exception ignored){}
            }

            return row;
        }

        public class LoadImage implements Runnable {
            protected ImageView imageView;
            protected Activity uiActivity;
            protected Context context;
            protected int position;
            protected String fileName;

            public LoadImage(ImageView imageView, Activity uiActivity, int position, Context context, String fileName) {
                this.imageView = imageView;
                this.uiActivity = uiActivity;
                this.context = context;
                this.fileName = fileName;
                this.position = position;
            }

            public void run() {
                try {
                    File imageFile = new File(context.getFilesDir() + "/" + fileName);
                    InputStream is = new FileInputStream(imageFile);

                    Bitmap userImage = BitmapFactory.decodeStream(is);

                    uiActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            imageView.setImageBitmap(Bitmap.createScaledBitmap(userImage, 300, 300, false));
                            imageView.setImageBitmap(userImage);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
