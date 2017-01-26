package net.dust_bowl.togetheragain;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by quaz4 on 26/01/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.BackgroundViewHolder>
{
    List<Background> backgrounds;

    RVAdapter(List<Background> inBackgrounds){
        this.backgrounds = inBackgrounds;
    }

    @Override
    public BackgroundViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.background_card_layout, parent, false);
        BackgroundViewHolder bvh = new BackgroundViewHolder(v);
        return bvh;
    }

    @Override
    public void onBindViewHolder(BackgroundViewHolder holder, int position)
    {
        holder.dateTime.setText(backgrounds.get(position).dateTime);
        holder.background.setImageResource(backgrounds.get(position).backgroundId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount()
    {
        return backgrounds.size();
    }

    public static class BackgroundViewHolder extends RecyclerView.ViewHolder
    {
        CardView cv;
        TextView dateTime;
        ImageView background;

        BackgroundViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            dateTime = (TextView)itemView.findViewById(R.id.dateTime);
            background = (ImageView)itemView.findViewById(R.id.background);
        }
    }
}
