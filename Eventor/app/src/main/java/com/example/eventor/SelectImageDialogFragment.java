package com.example.eventor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SelectImageDialogFragment extends DialogFragment {



    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;

    //widgets
    private ImageButton galleryImageButton;
    private ImageButton cameraImageButton;
    private Button cancelButton;

    //vars
    private OnChosenImageListener onChosenImageListener;
    private Context context;

    public SelectImageDialogFragment(Context context){
        this.context = context;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_image_dialog, container, false);

        galleryImageButton = (ImageButton) view.findViewById(R.id.gallery_image_button);
        galleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                getDialog().dismiss();
            }
        });
        cameraImageButton = (ImageButton) view.findViewById(R.id.camera_image_button);
        cameraImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
                getDialog().dismiss();
            }
        });
        cancelButton = (Button) view.findViewById(R.id.cancel_butten_fragment);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case REQUEST_GALLERY:
                    bitmap = getPictureFromGallery(data);
                    if (onChosenImageListener != null){
                        onChosenImageListener.onChosenImage(bitmap);
                    }
                    break;
                case REQUEST_CAMERA:
                    bitmap = getPhotoFromCamera(data);
                    if (onChosenImageListener != null){
                        onChosenImageListener.onChosenImage(bitmap);
                    }
                    break;
            }
        }
    }

    /**
     * This interface listening for receive image.
     */
    public interface OnChosenImageListener{
        public void onChosenImage(Bitmap chosenImage);
    }


    /**
     * This method use for set listener for choosing image from camera or gallery.
     * @param onChosenImageListener
     */
    public void setOnChosenImageListener(OnChosenImageListener onChosenImageListener){
        this.onChosenImageListener = onChosenImageListener;
    }







    /**
     * This method send the app to the gallery intent.
     */
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_GALLERY);
    }

    /**
     * This method gets the data that return from the gallery and returns it in bitmap image
     * @param data image bitmap
     * @return bitmap image
     */
    private Bitmap getPictureFromGallery(Intent data) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * This method send the app to the camera intent.
     */
    private void openCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE/*"android.media.action.IMAGE_CAPTURE"*/);
        getActivity().startActivityForResult(intent, REQUEST_CAMERA);
    }


    /**
     * This method gets the data that return from the camera and returns it in bitmap image
     * @param data that return from the camera
     * @return bitmat image
     */
    private Bitmap getPhotoFromCamera(Intent data) {
        // BitMap is data structure of image file which store the image in memory
        Bitmap photo = null;
        photo = (Bitmap) data.getExtras().get("data");
        return photo;
    }
}
