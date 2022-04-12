package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.components.ScrollButton;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterJobsPanel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AddJobWidget extends InternalSWidget {
	public static final int WIDTH = 238;
	private Button getJob, nextJob, previousJob;
	private CharacterJobsPanel parent;
	private List<String> jobsList;
	private int id;

	public AddJobWidget(CharacterJobsPanel panel, int x, int y) {
		super(panel.getParent(), x, y, WIDTH, 18, new TextComponent(""));
		this.parent = panel;
		this.getJob = new ScrollButton(panel.getParent(), x, y, 16, 16, new TextComponent("!!!"), button -> getJob());
		this.nextJob = new ScrollButton(panel.getParent(), x + 24, y, 16, 16, new TextComponent(">"),
				button -> nextJob());
		this.previousJob = new ScrollButton(panel.getParent(), x - 20, y, 16, 16, new TextComponent("<"),
				button -> previousJob());
		jobsList = new ArrayList<String>();
		jobsList.addAll(JobTemplate.getJobsList());
		id = 0;
		if (!jobsList.isEmpty()) {
			setCurrentJob(id);
		}
	}

	private void setCurrentJob(int id) {
		this.id = id;
		JobTemplate template = JobTemplate.getTemplate(jobsList.get(id));
		Component message = new TextComponent(template.getName());
		this.getJob.setMessage(message);
		this.getJob.setWidth(parent.getParent().getFont().width(message));
		this.nextJob.x = this.x + this.getJob.getWidth() + 4;
	}

	private void getJob() {
		if (jobsList.isEmpty())
			return;
		parent.getJob(JobTemplate.getTemplate(jobsList.get(id)));
	}

	private void nextJob() {
		if (jobsList.isEmpty())
			return;
		id++;
		if (id >= jobsList.size())
			id = 0;
		setCurrentJob(id);
	}

	private void previousJob() {
		if (jobsList.isEmpty())
			return;
		id--;
		if (id < 0)
			id = jobsList.size() - 1;
		setCurrentJob(id);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		double newY = mouseY + parent.getParent().getScrollData().baseY;
		if (getJob.mouseClicked(mouseX, newY, button) || nextJob.mouseClicked(mouseX, newY, button)
				|| previousJob.mouseClicked(mouseX, newY, button))
			return true;
		return false;
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible) {
//			if (currentJob != null)
//				drawString(stack, parent.getParent().getFont(), currentJob.getName(), this.x, this.y + baseY, 16777215);

			getJob.y = this.y + baseY;
			getJob.render(stack, mouseX, mouseY, partialTick);

			nextJob.y = this.y + baseY;
			nextJob.render(stack, mouseX, mouseY, partialTick);

			previousJob.y = this.y + baseY;
			previousJob.render(stack, mouseX, mouseY, partialTick);

			if (this.isHovered)
				renderToolTip(stack, mouseX, mouseY);
		}
	}

	@Override
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
//		super.renderToolTip(stack, mouseX, mouseY);
//		drawString(stack, parent.getParent().getFont(), job.getTooltip(), mouseX, mouseY, 16777215);
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		this.getJob.y = this.y;
		this.nextJob.y = this.y;
		this.previousJob.y = this.y;
		return diff;
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = !jobsList.isEmpty();
	}
}
