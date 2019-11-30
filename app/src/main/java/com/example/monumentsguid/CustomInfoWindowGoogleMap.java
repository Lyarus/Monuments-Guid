package com.example.monumentsguid;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Inflate the layouts for the info window, title and snippet.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoWindow = null;
        if (inflater != null) {
            infoWindow = inflater.inflate(R.layout.custom_info_contents, null);
            TextView title = infoWindow.findViewById(R.id.title);
            TextView comment = infoWindow.findViewById(R.id.comment);
            title.setText(Html.fromHtml(marker.getTitle()));
            comment.setText(marker.getSnippet());
        }
        return infoWindow;
    }
}
