package com.example.monumentsguid;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ItemGridAdapter extends BaseAdapter {

    private Context context;

    private List<String> nameList;
    private List<String> imageList;
    private List<String> idList;
    private Boolean isDefaultImage;

    ItemGridAdapter(Context c, List<String> idList, List<String> nameList, List<String> imageList, Boolean isDefaultImage) {
        this.context = c;
        this.nameList = nameList;
        this.imageList = imageList;
        this.idList = idList;
        this.isDefaultImage = isDefaultImage;
    }

    public int getCount() {
        return idList.size();
    }

    public String getItem(int position) {
        return idList.get(position);
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
            //int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            //int size = screenWidth * 2 / 7;
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
            //name.setText(nameList.get(position));
            name.setText(Html.fromHtml(nameList.get(position)));
        }
        if (image != null && isDefaultImage) {
            image.setBackgroundResource(R.drawable.default_monument);
        }
        if (image != null && imageList.get(position) != null) {
            new MapsActivity.DownloadImageTask(image).execute(imageList.get(position));
        }
        return gridView;
    }
}
