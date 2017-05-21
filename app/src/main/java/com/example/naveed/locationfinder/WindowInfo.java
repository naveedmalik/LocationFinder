package com.example.naveed.locationfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by Naveed on 5/19/2017.
 */

public class WindowInfo extends Activity
{
    ImageButton image;
    LinearLayout layout;
    static int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_window);
        //layout = (LinearLayout)findViewById(R.id.infoWindow);
       //image = (ImageButton)findViewById(R.id.image);
        /*layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getPicture(v);
            }
        });*/


    }

    public void getPicture(View view)
    {
        //ViewGroup viewGroup = (ViewGroup) findViewById(R.id.infoWindow);
        //ImageButton image_btn = (ImageButton) viewGroup.findViewById(R.id.image);
        String option[] = {"Open Camera","Open Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick An Option");
        builder.setItems(option, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:
                    {
                        openCamera();
                        break;
                    }
                    case 1:
                    {
                        selectImageFromGallery();
                        break;
                    }


                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectImageFromGallery()
    {
        Intent image_selection = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(image_selection,2);
    }



    private void openCamera()
    {
        File path = getFile();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT,path);
        startActivityForResult(camera,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            String path = "sdcard/MapImages/map_image"+ count++ +".jpg";
           // View v = findViewById(R.id.infoWindow);
            image = (ImageButton) findViewById(R.id.image);
            image.setImageDrawable(Drawable.createFromPath(path));
        }else
        if (requestCode == 2)
        {
            image = (ImageButton) findViewById(R.id.image);
            Uri image_uri = data.getData();
            image.setImageURI(image_uri);
        }

    }

    private File getFile()
    {
        File folder = new File("sdcard/MapImages");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        File image_path = new File(folder, System.currentTimeMillis()+"map_image.jpg");
        return image_path;
    }
}
