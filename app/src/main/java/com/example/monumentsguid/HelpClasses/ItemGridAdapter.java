package com.example.monumentsguid.HelpClasses;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monumentsguid.MapsActivity;
import com.example.monumentsguid.R;

import java.util.List;

public class ItemGridAdapter extends BaseAdapter {

    private Context context;

    private boolean isDefaultImage;

    private List<GridItem> gridItems;

    public ItemGridAdapter(Context c, boolean isDefaultImage, List<GridItem> gridItems) {
        this.context = c;
        this.isDefaultImage = isDefaultImage;
        this.gridItems = gridItems;
    }

    public int getCount() {
        return gridItems.size();
    }

    public GridItem getItem(int position) {
        return gridItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image = null;
        TextView name = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = new View(context);
            if (inflater != null) {
                gridView = inflater.inflate(R.layout.grid_item_layout, null);
            }
            gridView.setLayoutParams(new GridView.LayoutParams(300, 300));

            gridView.setPadding(8, 8, 8, 8);

            name = gridView.findViewById(R.id.grid_item_name);
            image = gridView.findViewById(R.id.grid_item_image);

        } else {
            gridView = convertView;
        }
        if (name != null) {
            name.setText(Html.fromHtml(gridItems.get(position).getName()));
        }
        if (image != null && !isDefaultImage) {
            image.setImageResource(0);
        }
        if (image != null && isDefaultImage && gridItems.get(position).getImage() != null) {
            try {
                new MapsActivity.DownloadImageTask(image).execute(gridItems.get(position).getImage());
            } catch (java.lang.NullPointerException e) {
                Log.d("Background Task", e.toString());
            }
        }
        if (gridItems.get(position).isActive()) {
            gridView.setBackgroundResource(R.drawable.rounded_corners_popup_blue);
        } else {
            gridView.setBackgroundResource(R.drawable.rounded_corners_popup_grey);
        }
        return gridView;
    }

}
