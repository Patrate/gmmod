package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.jobs.Job;
import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.AddJobWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.JobWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterJobsPanel extends ContainerPanel<CharacterSheet> {
	private CharacterSheet currentSheet;

	public CharacterJobsPanel(CharacterPanel panel, int ratio) {
		super(panel, ratio);
	}

	public void clearContent() {
		clearChildrens();
	}

	public void updateContent(CharacterSheet sheet) {
		currentSheet = sheet;
		clearChildrens();
		addChildren(new AddJobWidget(this, centerX(AddJobWidget.WIDTH), CharacterPanel.BORDER + this.y));
		int i = 1;
		for (Job job : sheet.getJobs()) {
			addChildren(new JobWidget(this, centerX(JobWidget.WIDTH), CharacterPanel.BORDER + this.y + i * 20, job));
			i++;
		}
		this.height = i * 20 + CharacterPanel.PADDING * 2 + CharacterPanel.BORDER * 2;
	}

	public void getJob(JobTemplate template) {
		if (currentSheet != null)
			currentSheet.addJob(template);
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		for (ScrollableWidget w : getChildrens()) {
			w.y += diff;
		}
		return diff;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
			for (ScrollableWidget w : getChildrens()) {
				w.render(poseStack, mouseX, mouseY, partialTick);
			}
		}
	}

	@Override
	protected void updateVisible() {
		this.visible = !getChildrens().isEmpty();
	}

	@Override
	public void init() {
		for (ScrollableWidget w : getChildrens()) {
			centerX(w);
		}
	}
}
