package net.dust_bowl.togetheragain;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by quaz4 on 24/01/2017.
 */

public class GalleryFragment extends Fragment
{
    View myView;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.gallery_layout, container, false);

        recyclerView = (RecyclerView)getView().findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        //TODO Get data list

        //TODO Init Adapter
        initialiseAdapter();

        return myView;
    }

    private void initialiseAdapter()
    {
        //TODO Replace null in RVAdapter constructor with List<Background> object
        RVAdapter adapter = new RVAdapter(null);
        recyclerView.setAdapter(adapter);
    }
}
