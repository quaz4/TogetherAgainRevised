package net.dust_bowl.togetheragain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
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

        showToast("Data changed");

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            //if("/IMAGE".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                Asset newBackground = map.getAsset("background");

                Bitmap bitmap = loadBitmapFromAsset(newBackground);

                FileOutputStream out = null;

                try
                {
                    out = new FileOutputStream("background.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    //PNG is a lossless format, the compression factor (100) is ignored
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if(out != null)
                        {
                            out.close();
                        }
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            //}
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
