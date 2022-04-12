package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.jobs.Job;
import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.components.ScrollButton;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterJobsPanel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JobWidget extends InternalSWidget {
	public static final int WIDTH = 238;
	private Job job;
//	private Button minus, plus;
	private Button levelUp;
	private CharacterJobsPanel parent;

	public JobWidget(CharacterJobsPanel panel, int x, int y, Job job) {
		super(panel.getParent(), x, y, WIDTH, 18, new TextComponent(job.getName()));
		this.parent = panel;
		this.job = job;
//		this.minus = new ScrollButton(panel.getParent(), x + 200, y, 16, 16, new TextComponent("-"),
//				button -> valueDown());
		this.levelUp = new ScrollButton(panel.getParent(), x + 200, y, 16, 16, new TextComponent("+"),
				button -> levelUp());
	}

	private void levelUp() {
		this.job.levelUp();
	}

//	private void valueUp() {
//		this.stat.setValue(this.stat.getValue() + 1);
//	}

//	private void valueDown() {
//		this.stat.setValue(this.stat.getValue() - 1);
//	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		double newY = mouseY + parent.getParent().getScrollData().baseY;
		if (levelUp.mouseClicked(mouseX, newY, button))
			return true;
		return false;
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible) {
			drawString(stack, parent.getParent().getFont(), job.getName(), this.x, this.y + baseY, 16777215);
			drawString(stack, parent.getParent().getFont(), "" + job.getLevel(), this.x + 150, this.y + baseY,
					16777215);

			levelUp.y = this.y + baseY;
			levelUp.render(stack, mouseX, mouseY, partialTick);

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
		this.levelUp.y = this.y;
		return diff;
	}
}
