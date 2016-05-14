package com.ellsworthcreations.vbtp20;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class VBTP {
	public static PlayerDatabase PlayerDB;
	public static Preferences MyGlobalPreferences;

	public static PlayerDatabase PlayerDB(Context ctx) {
		if(PlayerDB == null)
		{
			PlayerDB = new PlayerDatabase(ctx.getApplicationContext(), "playerDB");
		}

		return PlayerDB;
	}

    public static PlayerDatabase PlayerDB() {
        return PlayerDB;
    }

	public static Preferences GlobalPreferences(Context ctx) {
		if(MyGlobalPreferences == null)
		{
			MyGlobalPreferences = new Preferences(ctx.getApplicationContext());
		}

		return MyGlobalPreferences;
	}
//
//	public void onCreate()
//	{
//
//	}
//
//	public void onDestroy()
//	{
//		PlayerDB.close();
//	}
}
