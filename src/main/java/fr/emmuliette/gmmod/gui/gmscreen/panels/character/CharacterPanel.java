package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterPanel extends CustomScrollPanel {
	public final static int PADDING = 2, BORDER = 2, BAR_SIZE = 6;

	private AvatarPanel avatar;
	private CharacterDataPanel playerData;
	private CharacterJobsPanel jobs;
	private CharacterStatsPanel stats;

	public CharacterPanel(Minecraft mcIn, GmScreen parent, int widthIn, int heightIn, int top, int left) {
		super(mcIn, parent, widthIn, heightIn, top, left, PADDING, BORDER, BAR_SIZE);
	}

	@Override
	protected void init() {
		this.avatar = new AvatarPanel(this, 1);
		addWidget(avatar, 1);
		this.playerData = new CharacterDataPanel(this, 2);
		addWidget(playerData, 1);
		this.jobs = new CharacterJobsPanel(this, 1);
		addWidget(jobs, 2);
		this.stats = new CharacterStatsPanel(this, 1);
		addWidget(stats, 3);
	}

	public void setInfo(CharacterSheet sheet) {
		this.scrollDistance = 0f;
		this.stats.updateContent(sheet);
		this.jobs.updateContent(sheet);
		this.playerData.updateContent(sheet);
		this.avatar.updateContent(sheet);
		getContentHeight();

	}

	void clearInfo() {
		this.stats.clearContent();
		this.jobs.clearContent();
		this.playerData.clearContent();
		this.avatar.clearContent();
	}

	@Override
	public Font getFont() {
		return ((GmScreen) getParent()).getFontRenderer();
	}
}