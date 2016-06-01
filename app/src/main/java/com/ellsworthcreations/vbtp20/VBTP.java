package com.ellsworthcreations.vbtp20;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class VBTP {
	public static PlayerDatabase PlayerDB;
	public static ArrayList<Constraint> constraints = new ArrayList<>();

	// these are pairs/splits.

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

	public static int getSkillWeight(Context ctx, String key) {
		int weight = PreferenceManager.getDefaultSharedPreferences(ctx).getInt("weights_" + key, 1);
		return weight;
	}

	public static Boolean constraintsExist() {
		if(constraints.isEmpty()) { return false; }
		else { return true; }
	}

	public static ArrayList<Player> getAntiPairsForPlayer(Player player) {
		ArrayList<Player> ids = new ArrayList<>();
		for(Constraint c: constraints) {
			if(!c.split) { continue; }
			if(c.player1.equals(player)) {
				ids.add(c.player2);
			} else if(c.player2.equals(player)) {
				ids.add(c.player1);
			}
		}

		return ids;
	}

	public static ArrayList<Player> getPairsForPlayer(Player player) {
		ArrayList<Player> ids = new ArrayList<>();
		for(Constraint c: constraints) {
			if(c.split) { continue; }
			if(c.player1.equals(player)) {
				ids.add(c.player2);
			} else if(c.player2.equals(player)) {
				ids.add(c.player1);
			}
		}

		return ids;
	}

	public static void addConstraint(Player p1, Player p2, Boolean split) {
		Constraint c = new Constraint(p1, p2, split);
		for(Constraint c2: constraints) {
			if(c2.equals(c)) { return; }
		}

		constraints.add(c);
	}

	public static void removeConstraint(Constraint c) {
		for(Constraint c2: constraints) {
			if(c2.equals(c)) { constraints.remove(c2); break; }
		}
	}
}
