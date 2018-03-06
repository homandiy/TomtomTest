package com.homanhuang.tomtomtest;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.MutableContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.homanhuang.tomtomtest.utils.FileIO;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.webkit.WebView.HitTestResult.IMAGE_TYPE;
import static android.webkit.WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE;

public class ChangeBalloonImageActivity extends Activity {

    /* Log tag and shortcut */
    final static String TAG = "MYLOG CHANGE";
    public static void ltag(String message) { Log.i(TAG, message); }

    /* Toast shortcut */
    public static void msg(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /*
        Variabes
     */
    ConstraintLayout pickConstraintLayout;
    ConstraintLayout saveConstraintLayout;
    ConstraintLayout resultConstraintLayout;
    WebView imageWebView;
    String urlstr;
    String fileurl;

    CropImageView cropImageView;
    Button saveButton;

    //image folers
    File image_dir;
    Bitmap cropBitmap;
    ImageView resultImageView;

    FileIO mFileio;
    /*
        End of Variables
     */

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /*
        Buttons
     */
    public void saveImage(View v) {
        image_dir = getAlbumStorageDir("tomtom");
        String newFileName = mFileio.createFileName(null, "png");

        final Bitmap cropped = cropImageView.getCroppedImage();

        saveImageLocal(cropped, image_dir, newFileName);

        resultImageView.setImageBitmap(cropped);

        pickConstraintLayout.setVisibility(View.INVISIBLE);
        saveConstraintLayout.setVisibility(View.INVISIBLE);
        resultConstraintLayout.setVisibility(View.VISIBLE);

        resultImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickConstraintLayout.setVisibility(View.GONE);
                saveConstraintLayout.setVisibility(View.GONE);
                resultConstraintLayout.setVisibility(View.GONE);

                //return both bitmap back to main activity
                Intent openMainActivity= new Intent(ChangeBalloonImageActivity.this, MainActivity.class);

                ltag("transfer size: "+cropped.getByteCount());
                //bitmap transfer

                openMainActivity.putExtra("fileurl", fileurl);

                setResult(RESULT_OK, openMainActivity);

                ltag("Finish capture!");
                finish();
            }
        });
    }


    public void cancelImageSearch(View v) {
        finish();
    }

    public void tryAgain(View v) {
        saveConstraintLayout.setVisibility(View.INVISIBLE);
        pickConstraintLayout.setVisibility(View.VISIBLE);
    }

    private void saveImageLocal(Bitmap bitmap, final File imageDir, final String imageName) {
        final File myImageFile = new File(imageDir, imageName); // Create image file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myImageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fileurl = myImageFile.getAbsolutePath();
            ltag("Image saved to >>>" + fileurl);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), albumName);

        if (dir.exists() && dir.isDirectory()) {
            ltag("Directory existed");
        } else if (dir.mkdir()) {
            ltag("Directory created");
        } else {
            ltag("Directory not created");
        }
        return dir;
    }

    /*
        End of Buttons
     */

    //check back button
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if(imageWebView.canGoBack()){
            imageWebView.goBack();
        }

        this.doubleBackToExitPressedOnce = true;
        msg(getApplicationContext(), "Press again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private Bitmap Image64ToBitmap(String encodedString) {
        final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void loadCropImage() {
        pickConstraintLayout.setVisibility(View.INVISIBLE);
        saveConstraintLayout.setVisibility(View.VISIBLE);

        //setting
        cropImageView.setGuidelines(CropImageView.Guidelines.ON);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setAutoZoomEnabled(true);

        // subscribe to async event using cropImageView.setOnCropImageCompleteListener(listener)
        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
            }
        });
        cropBitmap = cropImageView.getCroppedImage();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_balloon_image);

        //file io
        mFileio = new FileIO();

        //crop view for thumbnail
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        saveButton = (Button) findViewById(R.id.saveButton);
        pickConstraintLayout = (ConstraintLayout) findViewById(R.id.pickConstraintLayout);
        saveConstraintLayout = (ConstraintLayout) findViewById(R.id.saveConstraintLayout);
        resultConstraintLayout = (ConstraintLayout) findViewById(R.id.resultConstraintLayout);
        resultImageView = (ImageView) findViewById(R.id.resultImageView);

        //load image website
        imageWebView = (WebView) findViewById(R.id.imageWebView);
        imageWebView.getSettings().setJavaScriptEnabled(true);
        imageWebView.setWebViewClient( new WebViewClient());
        imageWebView.clearCache(true);
        String imageUrl = "https://images.google.com/";
        imageWebView.loadUrl(imageUrl);


        imageWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

            WebView.HitTestResult result = imageWebView.getHitTestResult();

            //check image type
            if (result.getType() == IMAGE_TYPE || result.getType() == SRC_IMAGE_ANCHOR_TYPE) {
                urlstr = result.getExtra();
                ltag("HTML image: "+urlstr);

                //picasso cannot handle 64bit image
                //64bit image: data:image/jpeg;base64,......

                if (urlstr.contains("base64")) {
                    ltag("64bit image found. " + urlstr);
                    cropImageView.setImageBitmap(Image64ToBitmap(urlstr));
                    loadCropImage();
                } else {

                    if (URLUtil.isValidUrl(urlstr)) {

                        int index = 0;
                        Pattern p = Pattern.compile(".(?:jpg|gif|png|bmp)$");
                        Matcher m = p.matcher(urlstr);
                        if(m.find()) {
                            index = m.start();
                        }
                        //remove tail of some links
                        if (index > 0) {
                            urlstr = urlstr.substring(0, index + 4);
                        }

                        ltag("Valid URL: " + urlstr);

                        final Target mTarget = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                // do something with the Bitmap
                                ltag("bitmap size: "+bitmap.getByteCount());

                                cropBitmap = bitmap;
                                cropImageView.setImageBitmap(cropBitmap);
                                loadCropImage();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                ltag("Download Error!");
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };

                        Picasso.with(ChangeBalloonImageActivity.this).load(urlstr).into(mTarget);
                        cropImageView.setTag(mTarget);
                    }
                }
            }
            return false;
            }
        });
    }
}
