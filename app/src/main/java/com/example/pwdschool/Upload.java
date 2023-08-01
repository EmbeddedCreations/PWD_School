package com.example.pwdschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Upload extends AppCompatActivity {

    private static final int CAMERA_CODE = 101;
    private static final int RQS_OPEN_IMAGE = 1;
    Uri targetUri = null;
    TextView textUri;
    View.OnClickListener imageOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    showExif(targetUri);
                }
            };
    private ImageView iv_imgView;
    View.OnClickListener textUriOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (targetUri != null) {
                        Bitmap bm;
                        try {
                            bm = BitmapFactory.decodeStream(
                                    getContentResolver()
                                            .openInputStream(targetUri));
                            iv_imgView.setImageBitmap(bm);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            };
    private Button pickImageButton;
    private Button buttonUploadImage;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        iv_imgView = findViewById(R.id.image_view);
        pickImageButton = findViewById(R.id.pickimage);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        loader = findViewById(R.id.loader);
        textUri = findViewById(R.id.Dimensions);
        textUri.setOnClickListener(textUriOnClickListener);
        iv_imgView.setOnClickListener(imageOnClickListener);

        // Set button click listener for image upload
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_imgView.getDrawable() == null) {
                    Toast.makeText(Upload.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                } else {
                    showLoader();
                    // Simulate a 2-second delay for demonstration purposes
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                            // Handle image upload after the delay
                            Toast.makeText(Upload.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
            }
        });

        //For Getting Image From gallery
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog with two options
                AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
                builder.setTitle("Choose an option")
                        .setItems(new String[]{"Capture from Camera", "Select from Gallery"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // "Capture from Camera" option is selected
                                        ImagePicker.with(Upload.this)
                                                .cameraOnly()
                                                .crop()
                                                .compress(1024)
                                                .maxResultSize(720, 720)
                                                .start();
                                        break;
                                    case 1:
                                        // "Select from Gallery" option is selected
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("image/*"); // Allow all types of images (png, jpg, jpeg)
                                        startActivityForResult(intent, RQS_OPEN_IMAGE);
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        });

    }

    void showExif(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            /*
            How to convert the Uri to FileDescriptor, refer to the example in the document:
            https://developer.android.com/guide/topics/providers/document-provider.html
             */
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                /*
                ExifInterface (FileDescriptor fileDescriptor) added in API level 24
                 */
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                String exif = "Exif: " + fileDescriptor.toString();
                exif += "\nIMAGE_LENGTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
                exif += "\nIMAGE_WIDTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                exif += "\n DATETIME: " +
                        exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                exif += "\n TAG_MAKE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_MAKE);
                exif += "\n TAG_MODEL: " +
                        exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                exif += "\n TAG_ORIENTATION: " +
                        exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                exif += "\n TAG_WHITE_BALANCE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
                exif += "\n TAG_FOCAL_LENGTH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                exif += "\n TAG_FLASH: " +
                        exifInterface.getAttribute(ExifInterface.TAG_FLASH);
                exif += "\nGPS related:";
                exif += "\n TAG_GPS_DATESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
                exif += "\n TAG_GPS_TIMESTAMP: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
                exif += "\n TAG_GPS_LATITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                exif += "\n TAG_GPS_LATITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                exif += "\n TAG_GPS_LONGITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                exif += "\n TAG_GPS_LONGITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                exif += "\n TAG_GPS_PROCESSING_METHOD: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);

                parcelFileDescriptor.close();

                Toast.makeText(getApplicationContext(),
                        exif,
                        Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e,
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e,
                        Toast.LENGTH_LONG).show();
            }

            String strPhotoPath = photoUri.getPath();

        } else {
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == ImagePicker.REQUEST_CODE) {
            Uri uri = data.getData();
            targetUri = uri;
            textUri.setText(uri.toString());
            iv_imgView.setImageURI(uri);

            Uri dataUri = data.getData();
            if (requestCode == RQS_OPEN_IMAGE) {
                targetUri = dataUri;
                textUri.setText(dataUri.toString());
                iv_imgView.setImageURI(uri);
            }
            // Get the current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            System.out.println(dateTime);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "PERMISSION Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loader.setVisibility(View.GONE);
    }

}