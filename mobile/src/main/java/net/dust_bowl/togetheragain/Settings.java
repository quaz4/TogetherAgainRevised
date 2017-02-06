package net.dust_bowl.togetheragain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.net.URL;
import java.io.*;
import java.util.Random;

public class Settings extends AppCompatActivity {

    private static final String TAG = "Settings";
    GoogleApiClient mGoogleApiClient;
    boolean watchCom = false;

    private int PICK_IMAGE_REQUEST = 1;

    private class DownloadImage extends AsyncTask {
        @Override
        protected Bitmap doInBackground(Object[] params)
        {
            return download((String) params[0]);
        }

        @Override
        protected void onPostExecute(Object o)
        {
            ImageView imageView = (ImageView)findViewById(R.id.preview);
            imageView.setImageBitmap((Bitmap)o);
            sendToWear((Bitmap)o);
        }
    }

    private void sendToWear(Bitmap bitmap)
    {
        PutDataMapRequest request = PutDataMapRequest.create("/image");
        DataMap map = request.getDataMap();
        Asset asset = createAssetFromBitmap(bitmap);
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(1000);
        map.putInt("Integer", randomInt);
        map.putAsset("profileImage", asset);
        Wearable.DataApi.putDataItem(mGoogleApiClient, request.asPutDataRequest());
        showToast("Sending...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button viewHayley = (Button)findViewById(R.id.viewHayley);
        viewHayley.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                makeConnection();
                new DownloadImage().execute("Hayley");
            }
        });

        final Button receiveHayley = (Button)findViewById(R.id.receiveHayley);
        receiveHayley.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Bitmap downloaded = download("Hayley");
                ImageView imageView = (ImageView)findViewById(R.id.preview);
                imageView.setImageBitmap(downloaded);
            }
        });

        final Button viewWill = (Button)findViewById(R.id.viewWill);
        viewWill.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Bitmap downloaded = download("Will");
                ImageView imageView = (ImageView)findViewById(R.id.preview);
                imageView.setImageBitmap(downloaded);
            }

        });

        final Button receiveWill = (Button)findViewById(R.id.receiveWill);
        receiveWill.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Bitmap downloaded = download("Will");
                ImageView imageView = (ImageView)findViewById(R.id.preview);
                imageView.setImageBitmap(downloaded);
            }

        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        makeConnection();
    }

    public void launchGallery(View view)
    {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {

            Uri uri = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.preview);
                imageView.setImageBitmap(bitmap);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Bitmap download(String name)
    {
        try
        {
            URL url = new URL("http://www.dust-bowl.net/together_again_test/" + name);
            InputStream in = new BufferedInputStream(url.openStream());

            return BitmapFactory.decodeStream(in);
        }
        catch(IOException e)
        {
            //DO NOTHING
        }

        return null;

    }

    private void makeConnection() {
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
        {

        }
        else
        {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks()
                    {
                        @Override
                        public void onConnected(Bundle connectionHint)
                        {
                            Log.d(TAG, "onConnected: " + connectionHint);
                            watchCom = true;
                        }
                        @Override
                        public void onConnectionSuspended(int cause)
                        {
                            Log.d(TAG, "onConnectionSuspended: " + cause);
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener()
                    {
                        @Override
                        public void onConnectionFailed(ConnectionResult result)
                        {
                            Log.d(TAG, "onConnectionFailed: " + result);
                        }
                    })
                    .addApi(Wearable.API)
                    .build();

            mGoogleApiClient.connect();
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    private void showToast(String text)
    {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
