package fr.emmuliette.gmmod.jobs;

import java.util.List;

public class Job {
	private String name;
	private JobRessource ressource;
	private StatBlock statBlock;
	private List<JobSkill> jobSkills;

	public Job(String name, JobRessource ressource, StatBlock statBlock, List<JobSkill> jobSkills) {
		super();
		this.name = name;
		this.ressource = ressource;
		this.statBlock = statBlock;
		this.jobSkills = jobSkills;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JobRessource getRessource() {
		return ressource;
	}

	public void setRessource(JobRessource ressource) {
		this.ressource = ressource;
	}

	public StatBlock getStatBlock() {
		return statBlock;
	}

	public void setStatBlock(StatBlock statBlock) {
		this.statBlock = statBlock;
	}

	public List<JobSkill> getJobSkills() {
		return jobSkills;
	}

	public void setJobSkills(List<JobSkill> jobSkills) {
		this.jobSkills = jobSkills;
	}
	
	
	
}
