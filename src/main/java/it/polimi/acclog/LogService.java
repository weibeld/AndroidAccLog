package it.polimi.acclog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import java.io.File;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import org.apache.commons.io.FileUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.net.Uri;

public class LogService extends Service implements SensorEventListener {
	
	// Accelerometer reading interval in microseconds
	private final int readingInterval= 10000;

	// FTP server information
	private final String ftpHost         = "ftp://ftp.hostedftp.com";
	private final String ftpRemoteFolder = "/";
	private final String ftpUsername     = "weibel";
	private final String ftpPassword     = "antlabpolitecnicomilano";

	private File file;
	private SensorManager sm;

  	private static boolean isRunning = false;
  	public static boolean isRunning() {
  		return isRunning;
  	}

  	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    isRunning = true;

	    // Create new data file and write header
	    file = createNewFile();
	    appendToFile("ts,x,y,z\n");

	    // Initialise sensor manager
	    sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

	    // Get accelerometer sensor and register listener invoked every "readingInterval"
	    Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, acc, readingInterval);

	    return Service.START_STICKY;
	}

	// Create file to write logged data to
	private File createNewFile() {
    	Context context = AccLogActivity.getContext();
    	File    dir     = context.getExternalCacheDir();
    	String  date    = (String) new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
    	return new File(dir, "acc_" + date + ".csv");
	}
  
  	@Override
  	public void onDestroy() {
    	isRunning = false;
    	sm.unregisterListener(this);
		AccLogActivity.getContext().startActivity(getFtpIntent());
  	}

  	// Create intent for uploading the data file to an FTP server with AndFTP.
  	// Instructions on: http://www.lysesoft.com/products/andftp/intent.html
  	private Intent getFtpIntent() {
		Intent ftpIntent = new Intent();
		ftpIntent.setAction(Intent.ACTION_PICK);
		ftpIntent.setDataAndType(Uri.parse(ftpHost), "vnd.android.cursor.dir/lysesoft.andftp.uri");
		ftpIntent.putExtra("command_type", "upload");
		ftpIntent.putExtra("remote_folder", ftpRemoteFolder);
		ftpIntent.putExtra("ftp_username", ftpUsername);
		ftpIntent.putExtra("ftp_password", ftpPassword);
		ftpIntent.putExtra("local_file1", file.getAbsolutePath());
		return ftpIntent;
  	}

  	@Override
  	public IBinder onBind(Intent intent) {
    	return null;
  	}

  	/* From interface SensorEventListener. Handler for the accelerometer listener
  	 * set up in "onStartCommand(). */
	@Override
	public void onSensorChanged(SensorEvent event) {
	    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        float x = event.values[0];
	        float y = event.values[1];
	        float z = event.values[2];
	        // Note: there is also event.timestamp, but this is in nanoseconds
	        // since phone startup, and thus not practical
	        long ts = System.currentTimeMillis();
	        appendToFile(ts + "," + x + "," + y + "," + z + "\n");
	    }
	}

	private void appendToFile(String text) {
		try {
			FileUtils.writeStringToFile(file, text, true);
		}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
	}

  	/* From interface SensorEventListener. Not needed, but necessary to implement */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}