package com.example.monumentsguid.HelpClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.example.monumentsguid.R;
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

    public MarkerClusterRenderer(Context context, GoogleMap map, com.google.maps.android.clustering.ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        Bitmap bitmap = null;
        Canvas canvas;
        if (drawable != null) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return bitmap;
    }

    // tworzy widok pinezki zabytka
    @Override
    protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {

        Bitmap bitmap;
        if (item.getCustomImagePath() == null) {
            bitmap = getBitmapFromVectorDrawable(mContext, R.drawable.ic_marker_unknown);
        } else {
            bitmap = getBitmapFromVectorDrawable(mContext, R.drawable.ic_marker_default);
        }
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        markerOptions.icon(descriptor).snippet(item.getSnippet());
        markerOptions.icon(descriptor);
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
