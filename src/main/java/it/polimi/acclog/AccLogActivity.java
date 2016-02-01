package it.polimi.acclog;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.content.Context;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;

public class AccLogActivity extends Activity
{
	private static Context context;  // Reference to this activity
	private ToggleButton toggle;     // Toggle button for start logging

	public static Context getContext() {
    	return context;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        toggle  = (ToggleButton) findViewById(R.id.toggle);
        toggle.setTextOff("Start logging");
        toggle.setTextOn("Logging in progress...");
    	toggle.setChecked(LogService.isRunning());
    	toggle.setOnCheckedChangeListener(new MyToggleListener());
    }

    @Override
    public void onStop() {
    	super.onStop();
    }

    class MyToggleListener implements OnCheckedChangeListener {
	    @Override
	    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
	        // Toggle button OFF ==> ON
	        if (isChecked) {
        		startService(new Intent(AccLogActivity.getContext(), LogService.class));
	        	startNotification();
	        }
	        // Toggle button ON ==> OFF
	        else {
	        	endNotification();
	        	stopService(new Intent(AccLogActivity.getContext(), LogService.class));
	        	Context context = AccLogActivity.getContext();
	        }
	    }

	    // Create notification in notification bar (when logging is started)
	  	private void startNotification() {
		  	Context context = AccLogActivity.getContext();
		    PendingIntent clickPendingIntent = createNotificationIntent();
		    Notification n = new Notification.Builder(context)
		        .setContentTitle("AccLog")
		        .setContentText("Logging accelerometer data...")
		        .setSmallIcon(R.drawable.ic_notification)
		        .setOngoing(true)
		        .setContentIntent(clickPendingIntent)
		        .build();
		    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    nm.notify(1, n);
	  	}

	 	// Create intent for opening main activity (when clicking on notification)
	  	private PendingIntent createNotificationIntent() {
		  	Context context = AccLogActivity.getContext();
		    Intent clickIntent = new Intent(context, AccLogActivity.class);
		    // Use of TaskStackBuilder makes sure that clicking the "Back" button
		    // in the new activity leads the home screen (not to another activity)
		    TaskStackBuilder tsb = TaskStackBuilder.create(context);
		    tsb.addParentStack(AccLogActivity.class);
		    tsb.addNextIntent(clickIntent);
		    return tsb.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	  	}

		// Terminate notification
		private void endNotification() {
		  	Context context = AccLogActivity.getContext();
		  	NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    nm.cancel(1);
	  	}
	}
}
