package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidgetListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterStatsPanel extends ContainerPanel<CharacterSheet> implements StatWidgetListener {

	public CharacterStatsPanel(CharacterPanel panel, int ratio) {
		super(panel, ratio);
	}

	public void clearContent() {
		clearChildrens();
	}

	public void updateContent(CharacterSheet sheet) {
		clearChildrens();
		int i = 0;
		int biggest = 0;
		for (Stat stat : sheet.getStats()) {
			StatWidget w = new StatWidget(this, 0, CharacterPanel.BORDER + this.y + i * 20, stat);
			addChildren(w);
			biggest = (w.getTextWidth() > biggest) ? w.getTextWidth() : biggest;
			i++;
		}
		int newX = centerX(biggest + StatWidget.PADDING_TOTAL);
		for (ScrollableWidget w : getChildrens()) {
			w.x = newX;
			((StatWidget) w).setTextWidth(biggest);
		}
		this.height = i * 20 + CharacterPanel.PADDING * 2 + CharacterPanel.BORDER * 2;
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
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (ScrollableWidget w : getChildrens()) {
			if (w.mouseClicked(mouseX, mouseY, button))
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void init() {
		for (ScrollableWidget w : getChildrens()) {
			centerX(w);
		}
	}

	@Override
	public void onValueUp(Stat stat) {
		stat.setBaseValue(stat.getBaseValue() + 1);
	}

	@Override
	public void onValueDown(Stat stat) {
		stat.setBaseValue(stat.getBaseValue() - 1);
	}
}
