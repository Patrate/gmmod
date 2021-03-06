package fr.emmuliette.gmmod.gui.gmscreen.panels.jobs;

import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JobsPanel extends CustomScrollPanel {
	public final static int PADDING = 2, BORDER = 2, BAR_SIZE = 6;

	private StatBlockPanel stats;
	private JobDataPanel jobData;
	private JobTemplate loadedTemplate;
//	private AvatarPanel avatar;

	public JobsPanel(Minecraft mcIn, GmScreen parent, int widthIn, int heightIn, int top, int left) {
		super(mcIn, parent, widthIn, heightIn, top, left, PADDING, BORDER, BAR_SIZE);
	}

	@Override
	protected void init() {
//		this.avatar = new AvatarPanel(this, 1);
//		addWidget(avatar, 1);
		this.jobData = new JobDataPanel(this, 2);
		addWidget(jobData, 1);
		this.stats = new StatBlockPanel(this, 1);
		addWidget(stats, 2);

	}

	public void setInfo(JobTemplate job) {
		this.scrollDistance = 0f;
		this.jobData.updateContent(job);
		this.stats.updateContent(job);
//		this.avatar.updateContent(sheet);
		getContentHeight();

	}

	void clearInfo() {
		this.jobData.clearContent();
		this.stats.clearContent();
		setLoadedTemplate(null);
//		this.avatar.clearContent();
	}

	@Override
	public Font getFont() {
		return ((GmScreen) getParent()).getFontRenderer();
	}

	public void setLoadedTemplate(JobTemplate loadedTemplate) {
		this.loadedTemplate = loadedTemplate;
	}

	public JobTemplate getLoadedTemplate() {
		return loadedTemplate;
	}
}