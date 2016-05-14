package com.ellsworthcreations.vbtp20;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Iterator;

public class Player {
	private int playerID = -1;
	private String playerFirstName = "";
	private String playerLastName = "";
	private boolean playerActive = true;
	private Skills PlayerSkills = new Skills();
	private Gender gender = Gender.MALE;
	public static enum Gender { MALE, FEMALE };
	// 0 is male, 1 is female.

	private PlayerDatabase db;

	public Player(int nplayerID, String nplayerFirstName, String nplayerLastName, boolean nplayerActive, PlayerDatabase mydb)
	{
		playerID = nplayerID; // ID in database
		playerFirstName = nplayerFirstName;
		playerLastName = nplayerLastName;
		playerActive = nplayerActive;
		this.db = mydb;

		PlayerSkills = new Skills();
	}

	public Player()
	{
		PlayerSkills = new Skills();
	}

	public Player (int playerID, String playerFirstName, String playerLastName, boolean playerActive, int[] skills, PlayerDatabase mydb) {

		// create a new player object.
		this.playerID = playerID; // may be -1
		this.playerFirstName = playerFirstName;
		this.playerLastName = playerLastName;
		this.playerActive = playerActive;
		PlayerSkills = new Skills(skills);
		this.db = mydb;
	}
	
	public Player (int playerID, String playerFirstName, String playerLastName, boolean playerActive, Skills skills, PlayerDatabase mydb) {

		PlayerSkills = skills;
		
		// create a new player object.
		this.playerID = playerID; // may be -1
		this.playerFirstName = playerFirstName;
		this.playerLastName = playerLastName;
		this.playerActive = playerActive;
		this.db = mydb;
	}

	public long save()
	{
		return db.editPlayer(this);
	}
	
	public String getCSV()
	{
		String line = this.playerID + "," + this.playerFirstName + "," + this.playerLastName + ",";
		if(this.playerActive) { line += "1"; }
		else { line += "0"; }
		line += "," + this.getGenderAsInt();
		for(int i=0; i < Settings.colSkills.length; i++)
		{ line += "," + PlayerSkills.get(Settings.colSkills[i]); }
		return line;
	}
	
	public boolean loadFromCSV(String csvLine)
	{
		// remove any double quotes or single quotes immediately following a comma...
		csvLine.replaceAll("^[\"']", "");
		csvLine.replaceAll(",[\"']", "");
		csvLine.replaceAll("[\"'],", "");
		csvLine.replaceAll("[\"']$", "");
		
		String[] csvSplit = csvLine.split(",");
		//Log.d("PlayerFromCSV","line: " + csvLine + " ... array: " + csvSplit + " ... length: " + csvSplit.length);
		if(csvSplit.length < PlayerDatabase.getColumnList().length)
		{ return false; }
		else
		{
			// see if we already exist or not.
			this.playerID = Integer.parseInt(csvSplit[0]);
			this.playerFirstName = csvSplit[1];
			this.playerLastName = csvSplit[2];
			int tpa = Integer.parseInt(csvSplit[3]);
			if(tpa == 1) { this.playerActive = true; }
			else { this.playerActive = false; }
			this.setGenderAsInt(Integer.parseInt(csvSplit[4]));
			for(int i=PlayerDatabase.FieldsBeforeSkills; i < csvSplit.length; i++)
			{ this.PlayerSkills.put( Settings.colSkills[i-PlayerDatabase.FieldsBeforeSkills], Integer.parseInt(csvSplit[i])); }
			return true;
		}
	}
	
	public double distanceTo(Team team1)
	{
		Skills map2 = team1.getSkillAverages();
//		if(team1.size() == 0) { map2 = TeamPicker.averagePlayer.getSkills(); }
		return this.getSkills().distanceTo(map2);
	}

	public double distanceTo(Player aPlayer)
	{
		return this.getSkills().distanceTo(aPlayer.getSkills());
	}
	
	public Skills getSkills()
	{
		return PlayerSkills;
	}

	public String getName() {
		if(playerFirstName != null 
			&& playerFirstName.length() != 0
			&& !(" ".equals(playerFirstName)))
		{
			return playerFirstName + " " + playerLastName;
		}
		else { return playerLastName; }
	}
	
	public String getFirstName()
	{ return playerFirstName; }
	
	public String getLastName()
	{ return playerLastName; }

	public boolean isActive() {
		return playerActive;
	}

	public int getSkill(String skill) {
		return PlayerSkills.get(skill);
	}
	
	public void setSkill(String skill, int skillValue) {
		PlayerSkills.put(skill, skillValue);
	}

	public int getPlayerID() {
		return playerID;
	}
	
	public Integer getPlayerIDInteger()
	{ 
		return new Integer(playerID);
	}

	public void setActive(boolean tempActive) {
		playerActive = tempActive;
	}

	public int isActiveAsInt() {
		if(playerActive) { return 1; }
		else { return 0; }
	}

	public void setName(String firstName, String lastName) {
		playerFirstName = firstName;
		playerLastName = lastName;
	}

	public Player copy() {
		Player p = new Player(playerID, playerFirstName, playerLastName, playerActive, this.db);
		Iterator<String> itr = PlayerSkills.keySet().iterator();
		while(itr.hasNext())
		{
			String skill = itr.next();
			p.setSkill(skill, PlayerSkills.get(skill));
		}
		return p;
	}
	
	public boolean equals(Player p)
	{
		if(p.getPlayerID() == this.getPlayerID())
		{ return true; }
		else { return false; }
	}
	
	public Gender getGender()
	{ return this.gender; }
	
	public int getGenderAsInt()
	{
		if(this.gender == Gender.FEMALE) { return 1; }
		else { return 0; }
	}
	
	public void setGenderAsInt(int g)
	{
		if(g == 1)
		{ this.gender = Gender.FEMALE; }
		else { this.gender = Gender.MALE; }
	}
	
	public void setGender(Gender newGender)
	{ this.gender = newGender; }

	public boolean isMale()
	{ return this.gender == Gender.MALE; }

	public boolean isFemale()
	{ return this.gender == Gender.FEMALE; }

	@Override
	public String toString()
	{
		String ns = this.getName();
		return ns;
	}

	public void updateSkillsFromRows(TableLayout tl) {
		Log.d("updateSkillsFromRows", "updating...");
		for(int i=0; i < tl.getChildCount(); i++)
		{
			TableRow tr = (TableRow) tl.getChildAt(i);
			for (int j = 0; j < tr.getChildCount(); j++)
			{
				View x = tr.getChildAt(j);
				if (x instanceof RadioGroup)
				{
					Log.d("updateSkillsFromRows", "it is a radio group!");
					RadioGroup prg = (RadioGroup) x;
					Log.d("updateSkillsFromRows", "The player is " + this.getName());
					int idx = prg.indexOfChild(prg.findViewById(prg.getCheckedRadioButtonId()));
					Log.d("updateSkillsFromRows", "the skill is " + (String) tr.getTag() + " and the value is " + (idx + 1));
					this.setSkill((String) tr.getTag(), idx + 1);
				}
			}
		}
	}

	public TableRow[] generateSkillRows(Context c) {
		TableRow[] trs = new TableRow[Settings.colSkills.length];

		for(int i=0; i < Settings.colSkills.length; i++) {
			String skill = Settings.colSkills[i];
			TextView skillName = new TextView(c);
			skillName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			skillName.setText(skill);
			skillName.setTextColor(Color.DKGRAY);
			skillName.setPadding(0, 0, 10, 0);
			if(this.isActive())
			{ skillName.setTextColor(Color.WHITE); }

			TableRow tr = new TableRow(c);
			tr.addView(skillName);
			RadioGroup rg = new RadioGroup(c);
			rg.setTag(this);
			rg.setOrientation(RadioGroup.HORIZONTAL);
			for(int j=0; j < 5; j++)
			{
				RadioButton rb = new RadioButton(rg.getContext());
				rb.setText(Integer.toString(j + 1));
				rg.addView(rb);
			}
			int idx = this.getSkill(skill)-1;
			((RadioButton) rg.getChildAt(idx)).setChecked(true);

			//tr = new TableRow(this);
			tr.setTag(skill);
			tr.addView(rg);

			trs[i] = tr;
		}

		return trs;
	}
}
