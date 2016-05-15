package com.ellsworthcreations.vbtp20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerDatabase extends SQLiteOpenHelper {
	public static String dbName = "playerDB";
	public static final String playerTable = "Players";
	public static final String colID = "id";
	public static final String colFirstName = "firstName";
	public static final String colLastName = "lastName";
	public static final String colActive = "active";
	public static final String colPresent = "present";
	public static final String colGender = "gender";
	public static final int FieldsBeforeSkills = 6;
	private static final int DATABASE_VERSION = 12;

	// needs to be edited below to match colSkills.  Sort of.  This is the radio buttons for
	// skill values.
	public static HashMap<String, Integer> SkillToFieldIDs = new HashMap<String, Integer>();

	public static final int MaxSkillValue = 5;
	private static final String TAG = "PlayerDB";

	public static SQLiteDatabase myDB;

	public static String[] getColumnList()
	{
		ArrayList<String> cl = new ArrayList<String>();
		cl.add(colID); cl.add(colFirstName); cl.add(colLastName); cl.add(colActive); cl.add(colGender);
		for(int i=0;i<Settings.colSkills.length;i++) { cl.add(Settings.colSkills[i]); }
		String[] cla = new String[cl.size()];
		for(int i=0; i < cl.size(); i++)
		{ cla[i] = cl.get(i); }
		return cla;
	}

	public PlayerDatabase(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
		myDB = this.getWritableDatabase();

		Player[] allPlayers = this.getAllPlayers();
		if (allPlayers.length == 0) {
			// prepopulate for testing purposes.
			// in order: height, speed, throwing, catching, defense, competitive, experience
            Player x = new Player(0, "John", "Doe", true,
                    new Skills(new int[]{4, 5, 4, 5, 4, 4, 3}), this);
            x.setGender(Player.Gender.MALE);
            this.insertPlayer(x);
            x = new Player(0, "Jane", "Doe", true,
                    new Skills(new int[]{3, 4, 3, 3, 3, 2, 4}), this);
            x.setGender(Player.Gender.FEMALE);
            this.insertPlayer(x);
            this.insertPlayer(new Player(0, "Bob", "Deller", true,
                    new Skills(new int[]{3, 5, 4, 4, 4, 3, 4}), this));
            this.insertPlayer(new Player(0, "Larry", "Page", true,
                    new Skills(new int[]{4, 4, 2, 2, 4, 5, 3}), this));
		}
	}

//	public PlayerDatabase(Context context, String db_to_use) {
//		dbName = db_to_use;
//		super(context, dbName, null, DATABASE_VERSION);
//		myDB = this.getWritableDatabase();
//	}

	public void onDestroy()
	{
		myDB.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		createDatabase(db);
	}
	
	public void createDatabase(SQLiteDatabase db)
	{
		Log.d(TAG, "Creating table:");
		String query = "CREATE TABLE Players (id INTEGER PRIMARY KEY AUTOINCREMENT, firstName text NOT NULL, lastName text NOT NULL, active integer NOT NULL default 1, gender integer NOT NULL default 0";
		for(int i=0; i < Settings.colSkills.length; i++)
		{ query += ", " + Settings.colSkills[i] + " integer NOT NULL"; }
		query += ");";
		Log.d(TAG, query);
		db.execSQL(query);
	}
	
	public void dropDatabase(SQLiteDatabase db)
	{
		Log.d(TAG, "Dropping table:");
		String query = "DROP TABLE IF EXISTS " + PlayerDatabase.playerTable;
		Log.d(TAG, query);
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// try to get all the players out of the old version?
		Player[] players = getAllPlayersUpgrade(oldVersion, db);
		
		dropDatabase(db);
		createDatabase(db);
		
		Log.v("DatabaseUpgrade", "Trying to re-insert all the players...");
		
		// hopefully this will work.  insertPlayer expects myDB
		myDB = db;
		
		// specific database versions.
		if(oldVersion < 9 && newVersion >= 9)
		{
			// Version 9: Removed a few skills and added Experience
			// Version 10: Added gender.  Will default to male, so no change necessary.
			for(int i=0; i < players.length; i++)
			{
				for(int j=0; j < Settings.colSkills.length; j++)
				{
					String skill = Settings.colSkills[j];
					String skill2 = skill;
					// it gets set as experience...
					//if(skill == "Experience") { skill2 = "Cooperation"; }
					players[i].setSkill(skill, (int) Math.ceil(players[i].getSkill(skill2) / 2));
				}
				insertPlayer(players[i], true);
			}
		}

		// Version 11: Changed the skill names fairly significantly... but can probably
		// just re-insert?
		// simply re-insert, by default...
		else if(oldVersion >=9)
		{
			for(int i=0; i < players.length; i++)
			{
				insertPlayer(players[i], true);
			}
		}
	}
	
	public long insertPlayer(Player player)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		return insertPlayer(player, false);
	}
	
	public long insertPlayer(Player player, boolean ignoreExistsCheck)
	{
		//Log.d("Database","Database open: " + db.isOpen() + " ... readonly: " + db.isReadOnly());
		if(!myDB.isOpen())
		{
			//Log.d("Database","Trying to open database...");
			SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READWRITE);
		}
		//Log.v(TAG, "Database is open? :: " + db.isOpen());
		ContentValues cv = new ContentValues();
		cv.put(PlayerDatabase.colFirstName, player.getFirstName());
		cv.put(PlayerDatabase.colLastName, player.getLastName());
		if(!ignoreExistsCheck)
		{
			if(playerExistsByName(player.getFirstName(), player.getLastName()))
			{ return -1; }
		}
		cv.put(PlayerDatabase.colActive, Integer.toString(player.isActiveAsInt()));
		cv.put(PlayerDatabase.colGender, Integer.toString(player.getGenderAsInt()));
		for(int i=0;i<Settings.colSkills.length;i++)
		{
			cv.put(Settings.colSkills[i], player.getSkill(Settings.colSkills[i]));
		}
		long returnValue = myDB.insert(PlayerDatabase.playerTable, null, cv);
		return returnValue;
	}
	
	public long editPlayer(Player player)
	{
		//SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(PlayerDatabase.colFirstName, player.getFirstName());
		cv.put(PlayerDatabase.colLastName, player.getLastName());
		cv.put(PlayerDatabase.colActive, Integer.toString(player.isActiveAsInt()));
		cv.put(PlayerDatabase.colGender, Integer.toString(player.getGenderAsInt()));
		for(int i=0;i<Settings.colSkills.length;i++)
		{
			cv.put(Settings.colSkills[i], player.getSkill(Settings.colSkills[i]));
		}
		long returnValue = myDB.update(PlayerDatabase.playerTable, cv, PlayerDatabase.colID+"=?", new String[] { Integer.toString(player.getPlayerID()) });
		//db.close();
		return returnValue;
	}
	
	public Player[] getAllPlayersUpgrade(int oldVersion, SQLiteDatabase db)
	{
		String[] columns = new String[15];
		columns[0] = colID; 
		columns[1] = colFirstName; 
		columns[2] = colLastName; 
		columns[3] = colActive;
		
		int numberBeforeSkills = 4;
		if(oldVersion >= 10)
		{ columns[4] = colGender; numberBeforeSkills++; }
		for(int i=0; i<Settings.colSkills.length; i++)
		{
			// column changes since version 9.
			if(oldVersion < 9)
			{ 
				if(Settings.colSkills[i] == "Experience")
				{ columns[i+numberBeforeSkills] = "Cooperation"; continue; }
			}
			
			// that's the only change...
			columns[i+numberBeforeSkills] = Settings.colSkills[i];
		}
		
		String columnsString = columns[0];
		for(int i=1;i<columns.length;i++) { columnsString += ", " + columns[i]; }
		//Log.v(TAG, columnsString);
		
		Cursor c = db.query(playerTable, columns, null, null, null, null, null);
		c.moveToFirst();
		Player[] allPlayers = new Player[c.getCount()];
		//Player[] allPlayers = new Player[1];
		if(c.getCount() == 0)
		{
			allPlayers = new Player[0];
			c.close();
			return allPlayers;
		}
		do
		{
			boolean isActive = false;
			if(c.getInt(3) == 1) { isActive = true; }
			Player thisPlayer = new Player(c.getInt(0), c.getString(1), c.getString(2), isActive, this);
			if(oldVersion >= 10)
			{ thisPlayer.setGenderAsInt(c.getInt(4)); }
			
			for(int i=numberBeforeSkills; i < c.getColumnCount(); i++)
			{ thisPlayer.setSkill(Settings.colSkills[i-numberBeforeSkills], c.getInt(i)); }
			allPlayers[c.getPosition()] = thisPlayer;
		}
		while(c.moveToNext());
		c.close();
		return allPlayers;
	}
	
	public Player[] getAllPlayers()
	{
		return this.getPlayers(null, null, null, null, null);
	}
	
	public Player[] getAllPlayers(String selection, String[] selectionArgs)
	{
		return this.getPlayers(selection, selectionArgs, null, null, null);
	}
	
	public Player[] getActivePlayersSortedByName()
	{
		return this.getPlayers(PlayerDatabase.colActive+"=?", new String[] { "1" }, null, null, "lastName, firstName ASC");
	}
	
	public Player[] getAllPlayersSortedBySkill(String skill)
	{
		return this.getPlayers(null, null, null, null, skill + " DESC");
	}
	
	public Player[] getAllPlayersSortedByName()
	{
		return this.getPlayers(null, null, null, null, "lastName, firstName ASC");
	}
	
	// this is the one that really accesses the database.
	public Player[] getPlayers(String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
	{
		//SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = new String[15];
		columns[0] = colID; 
		columns[1] = colFirstName; 
		columns[2] = colLastName; 
		columns[3] = colActive;
		columns[4] = colGender;
		for(int i=0; i<Settings.colSkills.length; i++)
		{ columns[i+PlayerDatabase.FieldsBeforeSkills] = Settings.colSkills[i]; }
		
		String columnsString = columns[0];
		for(int i=1;i<columns.length;i++) { columnsString += ", " + columns[i]; }
		//Log.v(TAG, columnsString);
		if(!myDB.isOpen())
		{ myDB = this.getReadableDatabase(); }
		Cursor c = myDB.query(playerTable, columns, selection, selectionArgs, groupBy, having, orderBy);
		c.moveToFirst();
		Player[] allPlayers = new Player[c.getCount()];
		//Player[] allPlayers = new Player[1];
		if(c.getCount() == 0)
		{
			allPlayers = new Player[0];
			c.close();
			return allPlayers;
		}
		do
		{
			boolean isActive = false;
			if(c.getInt(3) == 1) { isActive = true; }
			
			// player id, first name, last name, and isActive
			Player thisPlayer = new Player(c.getInt(0), c.getString(1), c.getString(2), isActive, this);
			
			// gender
			thisPlayer.setGenderAsInt(c.getInt(4)); 
			
			// skills
			for(int i=PlayerDatabase.FieldsBeforeSkills; i < c.getColumnCount(); i++)
			{ thisPlayer.setSkill(Settings.colSkills[i-PlayerDatabase.FieldsBeforeSkills], c.getInt(i)); }
			allPlayers[c.getPosition()] = thisPlayer;
		}
		while(c.moveToNext());
		c.close();
		return allPlayers;
	}
	
	public boolean playerExistsByName(String playerFirstName, String playerLastName)
	{
		//Log.v(TAG, "Looking for a player with first/last of " + playerFirstName + "/" + playerLastName);
		Player[] players = this.getPlayers(PlayerDatabase.colFirstName+"=? AND "+PlayerDatabase.colLastName+"=?", new String[] { playerFirstName, playerLastName}, null, null, null);
		return players.length > 0;
	}
	
	public Player getPlayerByName(String playerFirstName, String playerLastName)
	{
		//Log.v(TAG, "Looking for a player with first/last of " + playerFirstName + "/" + playerLastName);
		Player[] players = this.getPlayers(PlayerDatabase.colFirstName+"=? AND "+PlayerDatabase.colLastName+"=?", new String[] { playerFirstName, playerLastName}, null, null, null);
		if(players.length > 0)
		{ return players[0]; }
		else { return null; }
	}
	
	public Player getPlayerByID(int playerID)
	{
		Player[] players = this.getPlayers(PlayerDatabase.colID+"=?", new String[] { Integer.toString(playerID) }, null, null, null);
		Player thePlayer = new Player();
		if(players.length > 0)
		{
			thePlayer = players[0];
		}
		return thePlayer;
	}
	
	public int deletePlayerByID(int playerID)
	{
		int returnValue = myDB.delete(playerTable, PlayerDatabase.colID+"=?", new String[] { Integer.toString(playerID) });
		return returnValue;
	}

	public void clear() {
		dropDatabase(this.getWritableDatabase());
		createDatabase(this.getWritableDatabase());
	}
}
