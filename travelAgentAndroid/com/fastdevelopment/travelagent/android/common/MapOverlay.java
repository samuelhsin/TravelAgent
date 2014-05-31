package com.fastdevelopment.travelagent.android.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.widget.Toast;

import com.fastdevelopment.travelagent.android.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MapOverlay extends Overlay {

	GeoPoint p;
	Context context;

	public MapOverlay(Context context, GeoPoint p) {
		super();
		this.p = p;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {

		super.draw(canvas, mapView, shadow, when);

		if (p != null) {
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			Bitmap bmp = BitmapFactory.decodeResource(ServerConfig.resource, R.drawable.pin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
		}

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {

		// when user lifts finger
		if (event.getAction() == 1) {
			this.p = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
			Toast.makeText(context, "Location: " + p.getLatitudeE6() / 1E6 + ", " + p.getLongitudeE6() / 1E6, Toast.LENGTH_LONG).show();
		}

		return false;
	}

}
