package com.foody.demoaar.lib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Util {
	public static void fadein(Activity activity, int id, int time) {
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setFillBefore(false);
		animation.setFillAfter(true);
		animation.setDuration(time);
		activity.findViewById(id).startAnimation(animation);
	}
	
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static void closeKeyboard(Activity activity) {
		if (activity.getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	public static int getWidthScreen(Context context, double ratio) {
		DisplayMetrics metries = context.getResources().getDisplayMetrics();
		return (int) (metries.widthPixels * ratio);
	}
	public static int getHeightScreen(Context context, double ratio) {
		DisplayMetrics metries = context.getResources().getDisplayMetrics();
		return (int) (metries.heightPixels * ratio);
	}

	public static String getDatabaseName() {
		String databaseName = Def.SDCARD_DB_NEW;

		return databaseName;
	}

	
	/** Move the file in oldLocation to newLocation. */
	public static void copyFile(File oldLocation, File newLocation)
			throws IOException {

		if (newLocation != null && oldLocation != null && oldLocation.exists()) {
			if (!newLocation.exists()) {
				newLocation.createNewFile();
			}

			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
			try {
				byte[] buff = new byte[8192];
				int numChars;
				while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
					writer.write(buff, 0, numChars);
				}
			} catch (IOException ex) {
				throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
			} finally {
				try {
					if (reader != null) {
						writer.close();
						reader.close();
					}
				} catch (IOException ex) {
					
				}
			}
		} else {
			throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
		}
	}
	
	
	public static String toMinuteString(int seconds) {
		return String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60);
	}
	
	
	public static int getImageId(Context context, String imgName) {
		return context.getResources().getIdentifier(imgName, "drawable", context.getPackageName());
	}
	
	
	public static Drawable getDrawable(Context context, String name) {

		int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		
		Drawable result = null;
		
		try {
			result = context.getResources().getDrawable(resourceId);
		} catch (Exception e) {
			return null;
		}
		return result;
	}
	
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	
	
	public static String decryption(String ciphertext, int key) {
		
		if(ciphertext == null || ciphertext.equals("")){
			return "";
		}

		String plaintext = "";
		int enKey = (key % 10) + 49;
		for (char c : ciphertext.toCharArray()) {
			plaintext = plaintext + Character.toString((char) ((c - enKey) == 64 ? 32 : (c - enKey)));
		}
		return plaintext;
	}
	
	public static boolean isVietnamese() {
		String lang = getLanguageCodeDevice();
		if (lang == null || !lang.equals("vi")) {
			return false;
		}

		return true;
	}
	
	
	public static String getLanguageCodeDevice() {
		String codeLanguage = "";

		try {
			codeLanguage = java.util.Locale.getDefault().getLanguage();
		} catch (Exception e) {
		}

		//exeption indonesia lang
		if(codeLanguage.equals("in")){
			return "id";
		}

		return codeLanguage;
	}
	
	public static boolean isAppInstall(Context context, String pack) {
		try {
			context.getPackageManager().getPackageInfo(pack, 0);
			return true;
		} catch (Exception e1) {
			return false;
		}
	}
	
	
	public static void gotoMarket(Context context, String gotoPack) {
		Uri uri;
		try {
			uri = Uri.parse("market://details?id=" + gotoPack);
			context.startActivity(new Intent(Intent.ACTION_VIEW, uri)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		} catch (Exception anfe) {
			try {
				uri = Uri
						.parse("http://play.google.com/store/apps/details?id="
								+ gotoPack);
				context.startActivity(new Intent(Intent.ACTION_VIEW, uri)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

			} catch (Exception e) {
			}
		}
	}
	public static String decoderString(String text) throws UnsupportedEncodingException {
		return URLDecoder.decode(text, "utf-8");
	}

	public static String encoderString(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "utf-8");
	}

	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private Util() {
	}

	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Deletes the contents of {@code dir}. Throws an IOException if any file
	 * could not be deleted, or if {@code dir} is not a readable directory.
	 */
	public static void deleteContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException("not a readable directory: " + dir);
		}
		for (File file : files) {
			if (file.isDirectory()) {
				deleteContents(file);
			}
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	public static void closeQuietly(/*Auto*/Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

//	public static void enableDisableView(View view, boolean enabled) {
//		view.setEnabled(enabled);
//		if ( view instanceof ViewGroup) {
//			ViewGroup group = (ViewGroup)view;
//
//			for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
//				enableDisableView(group.getChildAt(idx), enabled);
//			}
//		}
//	}

	public static long stringDate2Int(String strDate) {

		if (strDate == null) {
			return 0;
		}
		try {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Date date = df.parse(strDate);
			return date.getTime();
		} catch (ParseException e) {
		}
		return 0;
	}

	public static String int2DateString(long iDate) {
		if (iDate == 0)
			return "";
		Date date = new Date(iDate);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		return df.format(date);

	}

	public static String int2DateStringLong(long iDate) {
		if (iDate == 0)
			return "";
		Date date = new Date(iDate * 1000);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return df.format(date);

	}

}
