package fr.emmuliette.gmmod.gui.gmscreen.panels;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class StatPanel extends ScrollableWidget {
	private final List<StatWidget> statWidgets;

	public StatPanel(SheetPanel panel, int x, int y) {
		super(panel, x, y, panel.WIDTH, 0);
		statWidgets = new ArrayList<StatWidget>();
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	public void clearContent() {
		statWidgets.clear();
	}

	public void updateContent(CharacterSheet sheet) {
		statWidgets.clear();
		int i = 0;
		for (Stat stat : sheet.getStats()) {
			getParent();
			statWidgets.add(new StatWidget(this, centerX(StatWidget.WIDTH), SheetPanel.BORDER + this.y + i * 20, stat));
			i++;
		}
		getParent();
		this.height = i * 20 + SheetPanel.PADDING * 2 + SheetPanel.BORDER * 2;
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		for (StatWidget w : statWidgets) {
			w.y += diff;
		}
		return diff;
	}

	private void drawBackground(int baseY, Tesselator tess) {
		drawBorder(tess, this.x, this.y + baseY, getParent().WIDTH, this.height, SheetPanel.PADDING, SheetPanel.BORDER);
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible && !statWidgets.isEmpty()) {
			drawBackground(baseY, tess);
			// TODO borders
			for (StatWidget w : statWidgets) {
				w.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
			}
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (StatWidget w : statWidgets) {
			if (w.mouseClicked(mouseX, mouseY, button))
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
