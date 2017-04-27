package net.dust_bowl.togetheragain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by quaz4 on 8/02/2017.
 */

public class DataLayerListenerService extends WearableListenerService
{
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();
        mGoogleApiClient.connect();
        showToast("Service Running...");

    }

    private void showToast(final String text)
    {
        final Context MyContext = this;
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override public void run()
            {
                Toast toast1 = Toast.makeText(MyContext, text, Toast.LENGTH_LONG);
                toast1.show();
            }
        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        showToast("onDataChanged");

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events)
        {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                Asset newBackground = map.getAsset("background");
                Bitmap originalBitmap = loadBitmapFromAsset(newBackground);

                DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
                int displayWidth = metrics.widthPixels;
                int displayHeight = metrics.heightPixels;

                Bitmap bitmap = resize(originalBitmap, displayWidth, displayHeight);

                BufferedOutputStream out;

                try
                {
                    out = new BufferedOutputStream(openFileOutput("background.jpg", Context.MODE_WORLD_READABLE));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            String filename = "background.png";
            File env = Environment.getDataDirectory();
            File dest = new File(env, filename);
            boolean exists = dest.exists();

            if(exists)
            {
                showToast("Saving worked :)");
            }
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight)
    {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset)
    {
        showToast("Loading Asset");
        if(asset == null)
        {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(15000L, TimeUnit.MILLISECONDS);
        if(!result.isSuccess())
        {
            showToast("Failed");
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if(assetInputStream == null)
        {
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        showToast("Stopping Service");
    }
}
