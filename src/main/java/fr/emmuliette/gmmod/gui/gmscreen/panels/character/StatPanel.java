package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatPanel extends ContainerPanel {
	private final List<ScrollableWidget> childrens;

	public StatPanel(CharacterPanel panel, int ratio) {
		super(panel, ratio);
		childrens = new ArrayList<ScrollableWidget>();
	}

	public void clearContent() {
		childrens.clear();
	}

	public void updateContent(CharacterSheet sheet) {
		childrens.clear();
		int i = 0;
		for (Stat stat : sheet.getStats()) {
			getParent();
			childrens.add(new StatWidget(this, centerX(StatWidget.WIDTH), CharacterPanel.BORDER + this.y + i * 20, stat));
			i++;
		}
		getParent();
		this.height = i * 20 + CharacterPanel.PADDING * 2 + CharacterPanel.BORDER * 2;
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		for (ScrollableWidget w : childrens) {
			w.y += diff;
		}
		return diff;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
			for (ScrollableWidget w : childrens) {
				w.render(poseStack, mouseX, mouseY, partialTick);
			}
		}
	}

	@Override
	protected void updateVisible() {
		this.visible = !childrens.isEmpty();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (ScrollableWidget w : childrens) {
			if (w.mouseClicked(mouseX, mouseY, button))
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void init() {
		for (ScrollableWidget w : childrens) {
			centerX(w);
		}
	}
}
