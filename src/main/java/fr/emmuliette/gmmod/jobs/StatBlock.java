package fr.emmuliette.gmmod.jobs;

public class StatBlock {
	private float health;
	private float damage;
	private float defense;
	private float speed;
	private float crit;
	private float ressourceMax;
	private float ressourceRegen;

	public StatBlock(float health, float damage, float defense, float speed, float crit, float ressourceMax,
			float ressourceRegen) {
		this.health = health;
		this.damage = damage;
		this.defense = defense;
		this.speed = speed;
		this.crit = crit;
		this.ressourceMax = ressourceMax;
		this.ressourceRegen = ressourceRegen;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getDefense() {
		return defense;
	}

	public void setDefense(float defense) {
		this.defense = defense;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getCrit() {
		return crit;
	}

	public void setCrit(float crit) {
		this.crit = crit;
	}

	public float getRessourceMax() {
		return ressourceMax;
	}

	public void setRessourceMax(float ressourceMax) {
		this.ressourceMax = ressourceMax;
	}

	public float getRessourceRegen() {
		return ressourceRegen;
	}

	public void setRessourceRegen(float ressourceRegen) {
		this.ressourceRegen = ressourceRegen;
	}

}
