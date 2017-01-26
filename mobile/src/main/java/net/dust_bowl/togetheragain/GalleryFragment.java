package net.dust_bowl.togetheragain;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by quaz4 on 24/01/2017.
 */

public class GalleryFragment extends Fragment
{
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.gallery_layout, container, false);
        return myView;
    }
}
