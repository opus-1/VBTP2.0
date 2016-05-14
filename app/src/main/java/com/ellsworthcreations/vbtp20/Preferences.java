package com.ellsworthcreations.vbtp20;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {
	private Context context;
	private SharedPreferences sp;
	
	public Preferences(Context c)
	{
		this.context = c;
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void savePreference(String key, String value)
	{
		SharedPreferences.Editor editor = sp.edit();
	    editor.putString(key, value);
	    editor.commit();
	    Log.d("Preferences", "Saved preference: \"" + value + "\" went to key \"" + key + "\"");
	}
	
	public void savePreference(String key, boolean bool)
	{
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, bool);
		editor.commit();
		Log.d("Preferences", "Saved preference: \"" + bool + "\" went to key \"" + key + "\"");
	}
	
	public void savePreference(String key, int value)
	{
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
		Log.d("Preferences", "Saved preference: " + value + " went to key \"" + key + "\"");
	}
	
	public void savePreference(String key, float value)
	{
		SharedPreferences.Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
		Log.d("Preferences", "Saved preference: " + value + " went to key \"" + key + "\"");
	}
	
	public String getStringPreference(String key)
	{
		String preference = sp.getString(key, "");
		return preference;
	}
	
	public float getFloatPreference(String key)
	{
		float preference = sp.getFloat(key, -1);
		return preference;
	}
	
	public boolean getBooleanPreference(String key)
	{
		boolean preference = sp.getBoolean(key, false);
		return preference;
	}
	public int getIntegerPreference(String key)
	{
		int preference = sp.getInt(key, -1);
		return preference;
	}
}
