package fr.emmuliette.gmmod.jobs;

public class JobSkill {
	private Skill skill;
	private int level;
	private int cost;

	public JobSkill(Skill skill, int level, int cost) {
		this.skill = skill;
		this.level = level;
		this.cost = cost;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	
}
