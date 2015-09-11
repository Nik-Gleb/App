package ru.touchin.myproject.utils;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ListView;
import java.util.Random;
import android.os.Bundle;
import android.util.Log;
import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

public final class Utils {

	/**
	 * Force Change Configuration
	 * 
	 * @param context Application Context
	 */
	@SuppressWarnings("unused")
	public static void forceChangeConfiguration(Context context) {

		try {
			int k = context.getResources().getConfiguration().keyboardHidden == 2 ? 1 : 2;

			final Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
			final Object am = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
			final Object config = am.getClass().getMethod("getConfiguration").invoke(am);
			config.getClass().getDeclaredField("keyboardHidden").setInt(config, k);
			am.getClass().getMethod("updateConfiguration", android.content.res.Configuration.class)
			.invoke(am, config);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** @return Random String. */
	@SuppressWarnings("unused")
	public static String randomString() {
		final Random generator = new Random();
		final StringBuilder randomStringBuilder = new StringBuilder();
		int randomLength = generator.nextInt(16);
		char tempChar;
		for (int i = 0; i < randomLength; i++) {
			tempChar = (char) (generator.nextInt(96) + 32);
			randomStringBuilder.append(tempChar);
		}
		return randomStringBuilder.toString();
	}

	/**
	 * Print the contents of a Bundle to Logcat.
	 *
	 * @param logTag tag for logging
	 * @param bundle bundle
	 */
	@SuppressWarnings("unused")
	public static void printBundle(String logTag, Bundle bundle) {
		Log.d(logTag, "/*** Bundle ***");
		for (String key : bundle.keySet())
			Log.d(logTag, " * " + key + ": " + bundle.get(key));
		Log.d(logTag, " **************/");
	}


	/**
	 * Releases all references in Root-View to avoid memory leaks.
	 * 
	 * @param view Root View
	 */
	public static void unbindReferences(View view) {

		try {
			unbindViewReferences(view);
			if (view instanceof ViewGroup)
				unbindViewGroupReferences((ViewGroup) view);
		} catch (Throwable extention) {
			/*
			 * whatever exception is thrown just ignore it because a crash is
			 * always worse than this method not doing what it's supposed to do.
			 */
		}
	}

	/**
	 * Unbinds all references in ViewGroup.
	 * 
	 * @param viewGroup some View Group
	 */
	private static void unbindViewGroupReferences(ViewGroup viewGroup) {
		final int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View view = viewGroup.getChildAt(i);
			unbindViewReferences(view);
			if (view instanceof ViewGroup)
				unbindViewGroupReferences((ViewGroup) view);
		}
		try {
			viewGroup.removeAllViews();
		} catch (Throwable exception) {
			/*
			 * AdapterViews, ListViews and potentially other ViewGroups don't
			 * support the removeAllViews operation.
			 */
		}
	}

	/**
	 * Unbinds all references in View.
	 * 
	 * @param view some View
	 */
	private static void unbindViewReferences(View view) {
		/*
		 * set all listeners to null (not every view and not every API level
		 * supports the methods)
		 */
		try {view.setOnClickListener(null);}catch (Throwable ignored) {}
		try {view.setOnCreateContextMenuListener(null);} catch (Throwable ignored) {}
		try {view.setOnFocusChangeListener(null);} catch (Throwable ignored) {}
		try {view.setOnKeyListener(null);} catch (Throwable ignored) {}
		try {view.setOnLongClickListener(null);} catch (Throwable ignored) {}
		try {view.setOnClickListener(null);} catch (Throwable ignored) {}

		// set background to null
		Drawable d = view.getBackground();
		if (d != null)
			d.setCallback(null);
		if (view instanceof ImageView) {
			final ImageView imageView = (ImageView) view;
			d = imageView.getDrawable();
			if (d != null)
				d.setCallback(null);
			imageView.setImageDrawable(null);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				clearImageViewBackground_16andLater(imageView);
			else
				clearImageViewBackground_before16(imageView);

		}

		// destroy webview
		if (view instanceof WebView) {
			view.destroyDrawingCache();
			((WebView) view).destroy();
		}

		if (view instanceof AbsListView) {
			((AbsListView) view).setOnItemClickListener(null);
			((AbsListView) view).setOnItemLongClickListener(null);
			((AbsListView) view).setOnItemSelectedListener(null);
		}

		if (view instanceof ListView) {
			((ListView) view).setOverscrollHeader(null);
			((ListView) view).setOverscrollFooter(null);
			((ListView) view).setDivider(null);
		}
	}

	/** @param imageView to cleaning bg for API less 16. */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.BASE)
	private static void clearImageViewBackground_before16(ImageView imageView) {
		imageView.setBackgroundDrawable(null);
	}

	/** @param imageView to cleaning bg for API larger or equals 16. */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static void clearImageViewBackground_16andLater(ImageView imageView) {
		imageView.setBackground(null);
	}

}
