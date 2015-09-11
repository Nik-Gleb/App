package ru.touchin.myproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import android.app.Application;
import android.os.Debug;
import android.os.Environment;
import android.os.Process;
import android.os.StrictMode;
import android.widget.Toast;

public final class App extends Application {
	
    /** The number of device cores. */
    @SuppressWarnings("unused")
	public static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();
	/** The index of Linux Thread Priority, that will use for Main Thread. */
	private static final int MAIN_THREAD_PRIORITY_INDEX = -20;

	/** {@inheritDoc} */
	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildConfig.DEBUG)
		    Toast.makeText(this, "DEBUG", Toast.LENGTH_SHORT).show();
		initStrictMode();
	}


	
	/** */
	private static void initStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().detectCustomSlowCalls().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().detectActivityLeaks().build());

			System.setErr(new PrintStreamStrictModeKills());

		} else
			Process.setThreadPriority(Process.myTid(), MAIN_THREAD_PRIORITY_INDEX);
	}

	private static final class PrintStreamStrictModeKills extends PrintStream {

		/** {@inheritDoc} */
		public PrintStreamStrictModeKills() {
			super(System.err);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(File file) throws FileNotFoundException {
			super(file);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(String fileName) throws FileNotFoundException {
			super(fileName);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(OutputStream out, boolean autoFlush) {
			super(out, autoFlush);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(File file, String charsetName)
				throws FileNotFoundException, UnsupportedEncodingException {
			super(file, charsetName);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(String fileName, String charsetName)
				throws FileNotFoundException, UnsupportedEncodingException {
			super(fileName, charsetName);
		}

		/** {@inheritDoc} */
		@SuppressWarnings("unused")
		public PrintStreamStrictModeKills(OutputStream out, boolean autoFlush, String charsetName)
				throws UnsupportedEncodingException {
			super(out, autoFlush, charsetName);
		}

		/** {@inheritDoc} */
		@Override
		synchronized public final void println(String str) {
			super.println(str);
			if (str.startsWith("StrictMode VmPolicy violation with POLICY_DEATH;")) {
				// StrictMode is about to terminate us... do a heap dump!
				try {
					final File dir = Environment.getExternalStorageDirectory();
					final File file = new File(dir, "strictmode-violation.hprof");
					super.println("Dumping HPROF to: " + file);
					Debug.dumpHprofData(file.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
