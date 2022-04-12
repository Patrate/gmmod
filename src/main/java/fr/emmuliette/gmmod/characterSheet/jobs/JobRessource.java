package fr.emmuliette.gmmod.characterSheet.jobs;

import java.awt.Color;

public class JobRessource {
	private String name;
	private int defaultMax;
	private Color color;
	private RessourceRegeneration regeneration;

	public JobRessource(String name, int defaultMax, Color color, RessourceRegeneration regeneration) {
		super();
		this.name = name;
		this.defaultMax = defaultMax;
		this.color = color;
		this.regeneration = regeneration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDefaultMax() {
		return defaultMax;
	}

	public void setDefaultMax(int defaultMax) {
		this.defaultMax = defaultMax;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public RessourceRegeneration getRegeneration() {
		return regeneration;
	}

	public void setRegeneration(RessourceRegeneration regeneration) {
		this.regeneration = regeneration;
	}

}
