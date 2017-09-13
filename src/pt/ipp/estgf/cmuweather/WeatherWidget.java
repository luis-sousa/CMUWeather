package pt.ipp.estgf.cmuweather;



import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.EventLogTags.Description;
import android.util.Log;
import android.widget.RemoteViews;

public class WeatherWidget extends BroadcastReceiver {

	
	private String placeName;
	private String placeTemp;
	private String placeDescription;
	private int iconId;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		ComponentName thisWidget = new ComponentName(context, WeatherWidget.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		
		if(intent.getAction().equals("pt.ipp.estgf.cmuweather.UPDATE_WIDGET")){
			
			placeName = intent.getStringExtra("Place");
			placeTemp = intent.getStringExtra("Temperature");
			placeDescription = intent.getStringExtra("Description");
			iconId = intent.getIntExtra("Icon", 0);
		}
		
		onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
	}
	
	

	public void onUpdate(Context context, AppWidgetManager widgetMan, int[] widgetIds){
		
		final int N = widgetIds.length;
		
		for(int i=0; i<N; i++){
			int appWidgetId = widgetIds[i];
			
			
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
			
			views.setTextViewText(R.id.wg_place, placeName);
			views.setTextViewText(R.id.wg_current_temp, placeTemp);
			views.setTextViewText(R.id.wg_condition, placeDescription);
			views.setImageViewResource(R.id.wg_weather_image, iconId);
			
			
			widgetMan.updateAppWidget(appWidgetId, views);
		}
	}

}
