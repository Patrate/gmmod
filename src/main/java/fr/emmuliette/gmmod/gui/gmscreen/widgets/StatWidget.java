package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.components.ScrollButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatWidget extends InternalSWidget {
	public static final int PADDING_TOTAL = 58;
	private Stat stat;
	private Button minus, plus;
	private StatWidgetListener parent;
	private int scoreX;

	public StatWidget(StatWidgetListener parent, int x, int y, Stat stat) {
		super(parent.getParent(), x, y, 0, 18, stat.getName());
		this.parent = parent;
		this.stat = stat;
		this.minus = new ScrollButton(parent.getParent(), x, y, 16, 16, new TextComponent("-"), button -> valueDown());
		this.plus = new ScrollButton(parent.getParent(), x, y, 16, 16, new TextComponent("+"), button -> valueUp());
		this.scoreX = x;
	}

	public int getTextWidth() {
		return parent.getParent().getFont().width(stat.getName());
	}

	public void setTextWidth(int width) {
		this.width = width + 58; // pad (4) + valeur (24) + pad (4) + bouton (16) + pad (2) + bouton (16)
		this.scoreX = this.x + width + 4;
		this.minus.x = this.scoreX + 28;
		this.plus.x = this.minus.x + 18;
	}

	private void valueUp() {
		parent.onValueUp(stat);
	}

	private void valueDown() {
		parent.onValueDown(stat);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		double newY = mouseY + parent.getParent().getScrollData().baseY;
		if (minus.mouseClicked(mouseX, newY, button) || plus.mouseClicked(mouseX, newY, button))
			return true;
		return false;
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible) {
			String statValue = "" + stat.getBaseValue() + ((stat.hasBonus()) ? " (+" + stat.getTotalBonus() + ")" : "");
			drawString(stack, parent.getParent().getFont(), stat.getName(), this.x, 6 + this.y + baseY, 16777215);
			drawString(stack, parent.getParent().getFont(), statValue, scoreX, 6 + this.y + baseY, 16777215);

			this.minus.y = this.y + baseY;
			this.plus.y = this.y + baseY;
			minus.active = stat.getValue() > stat.getMin();
			plus.active = stat.getValue() < stat.getMax();
			minus.render(stack, mouseX, mouseY, partialTick);
			plus.render(stack, mouseX, mouseY, partialTick);

			if (this.isHovered)
				renderToolTip(stack, mouseX, mouseY);
		}
	}

	@Override
	public void renderToolTip(PoseStack stack, int mouseX, int mouseY) {
		drawString(stack, parent.getParent().getFont(), stat.getTooltip(), mouseX, mouseY, 16777215);
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		this.minus.y = this.y;
		this.plus.y = this.y;
		return diff;
	}
}
