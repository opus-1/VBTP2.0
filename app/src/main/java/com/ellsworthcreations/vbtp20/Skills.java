package com.ellsworthcreations.vbtp20;

import java.util.HashMap;
import java.util.Iterator;

public class Skills extends HashMap<String, Integer> {

	private static final long serialVersionUID = 1L;
	
	public Skills(int[] startingSkills)
	{
		if(startingSkills.length >= Settings.colSkills.length)
		{
			for(int i=0; i < Settings.colSkills.length; i++)
			{ put(Settings.colSkills[i], startingSkills[i]); }
		}
	}
	
	public Skills()
	{
		for(int i=0; i < Settings.colSkills.length; i++)
		{ put(Settings.colSkills[i], 1); }
	}

	public void add(Skills otherMap)
	{
		Iterator<String> itr = this.keySet().iterator();
		while(itr.hasNext())
		{ 
			String skill = itr.next();
			put(skill, (get(skill)+otherMap.get(skill)));
		}
	}
	
	public int cumulativeSkills()
	{
		int cumSkills = 0;
		Iterator<String> itr = this.keySet().iterator();
		while(itr.hasNext())
		{ cumSkills += get(itr.next()); }
		return cumSkills;
	}
	
	public double distanceTo(Team team1)
	{
		Skills map2 = team1.getSkillAverages();
//		if(team1.size() == 0) { map2 = TeamPicker.averagePlayer.getSkills(); }
		return this.distanceTo(map2);
	}
	
	public double distanceTo(Player p2)
	{
		return this.distanceTo(p2.getSkills());
	}
	
	public double distanceTo(Skills map2)
	{
		double distance = 0;
		Iterator<String> hitr = this.keySet().iterator();
		while(hitr.hasNext())
		{ 
			String key = hitr.next();
			//Log.d("MapDistance", "Raw distance between skill " + key + "(" + map1.get(key) + "-" + map2.get(key) + "): " + (map1.get(key)-map2.get(key)));
			int weight = VBTP.MyGlobalPreferences.getIntegerPreference("Weights_"+key);
			if(weight == -1 || weight == 0) { weight = 1; }
			double difference = (this.get(key)*weight) - (map2.get(key)*weight);
			distance += (difference*difference);
		}
		//Log.v("MapDistance", "Raw distance: " + distance);
		distance = Math.sqrt(distance);
		//Log.v("MapDistance", "Square root distance: " + distance);
		return distance;
	}
	

}
