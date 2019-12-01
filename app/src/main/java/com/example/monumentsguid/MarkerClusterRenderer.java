package com.example.monumentsguid;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MarkerClusterRenderer extends DefaultClusterRenderer<ClusterItem> {
    private static final int SHOULD_RENDER_AS_CLUSTER_NUMBER = 3;

    private final Context mContext;
    private final IconGenerator mClusterIconGenerator;

    MarkerClusterRenderer(Context context, GoogleMap map, com.google.maps.android.clustering.ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }

    // tworzy widok pinezki zabytka
    @Override
    protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {

        final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pinezka);
        markerOptions.icon(markerDescriptor).snippet(item.getSnippet());
    }

    // tworzy widok pinezki clustera
    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterItem> cluster,
                                           MarkerOptions markerOptions) {
        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(mContext, R.drawable.background_circle));
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterItem> cluster) {
        return cluster.getSize() > SHOULD_RENDER_AS_CLUSTER_NUMBER;
    }
}
