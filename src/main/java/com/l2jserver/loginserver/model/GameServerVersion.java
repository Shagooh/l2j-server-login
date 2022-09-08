package com.l2jserver.loginserver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Game Server version.
 * @author Zoey76
 * @version 2.6.1.8
 */
public enum GameServerVersion {
	// Saga I: The Chaotic Chronicle
	PRELUDE_CLOSED_BETA(-1, "Prelude Closed Beta"),
	PRELUDE(0, "Prelude"),
	HARBINGERS_OF_WAR(1, "Chronicle I: Harbingers of War"),
	AGE_OF_SPLENDOR(2, "Chronicle II: Age of Splendor"),
	RISE_OF_DARKNESS(3, "Chronicle III: Rise of Darkness"),
	SCIONS_OF_DESTINY(4, "Chronicle IV: Scions of Destiny"),
	OATH_OF_BLOOD(5, "Chronicle V: Oath of Blood"),
	// Saga II: The Chaotic Throne
	INTERLUDE(6, "Interlude"),
	// Throne I
	THE_KAMAEL(7, "The Kamael"),
	HELLBOUND(8, "Hellbound"),
	// Throne II
	GRACIA_1(9, "Gracia"),
	GRACIA_2(10, "Gracia Part 2"),
	GRACIA_FINAL(11, "Gracia Final"),
	EPILOGUE(12, "Gracia Epilogue"),
	FREYA(13, "Freya"),
	HIGH_FIVE(14, "High Five"),
	// Saga III: Goddess of Destruction
	// Chapter 1
	AWAKENING(15, "Awakening"),
	HARMONY(16, "Harmony"),
	// Chapter 2
	TAUTI(17, "Tauti"),
	GLORY_DAYS(18, "Glory Days"),
	// Chapter 3
	LIVINDOR(19, "Lindvior"),
	// Saga IV: Epic Tale of Aden
	// Episode 1
	ERTHEIA(20, "Ertheia"),
	// Episode 2
	VALIANCE(21, "Valiance"),
	// Episode 2.5
	INFINITE_ODYSSEY(22, "Infinite Odyssey"),
	UNDERGROUND(23, "Underground"),
	HELIOS(24, "Helios"),
	GRAND_CRUSADE(25, "Grand Crusade"),
	SALVATION(26, "Salvation"),
	ETINAS_FATE(27, "Etina's Fate"),
	FAFURION(28, "Fafurion"),
	PREULDE_OF_WAR(29, "Prelude of War"),
	DAWN_OF_HEROES(30, "Dawn of Heroes");
	
	static final Map<Integer, String> versions = new HashMap<>();
	
	int id;
	
	String description;
	
	static {
		for (var p : GameServerVersion.values()) {
			versions.put(p.getId(), p.getDescription());
		}
	}
	
	GameServerVersion(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static Map<Integer, String> versions() {
		return versions;
	}
	
	public static String valueOf(int id) {
		return versions.get(id);
	}
}
