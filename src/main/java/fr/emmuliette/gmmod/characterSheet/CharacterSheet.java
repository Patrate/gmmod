package fr.emmuliette.gmmod.characterSheet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.emmuliette.gmmod.jobs.Job;

public class CharacterSheet {

	private Map<Job, Integer> jobs;
	private UUID uuid;

	public CharacterSheet(UUID uuid) {
		this.uuid = uuid;
		jobs = new HashMap<Job, Integer>();
	}

	public Map<Job, Integer> getJobs() {
		return jobs;
	}

	public void setJobs(Map<Job, Integer> jobs) {
		this.jobs = jobs;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
