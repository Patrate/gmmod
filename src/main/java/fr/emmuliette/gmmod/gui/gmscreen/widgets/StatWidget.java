package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.panels.StatPanel;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatWidget extends InternalSWidget {
	public static final int WIDTH = 238;
	private Stat stat;
	private Button minus, plus;
	private StatPanel parent;

	public StatWidget(StatPanel panel, int x, int y, Stat stat) {
		super(panel.getParent(), x, y, WIDTH, 18, stat.getName());
		this.parent = panel;
		this.stat = stat;
		this.minus = new ScrollButton(panel.getParent(), x + 200, y, 16, 16, new TextComponent("-"),
				button -> valueDown());
		this.plus = new ScrollButton(panel.getParent(), x + 220, y, 16, 16, new TextComponent("+"),
				button -> valueUp());
	}

	private void valueUp() {
		this.stat.setValue(this.stat.getValue() + 1);
		GmMod.logger().info("+1 " + stat.getName());
	}

	private void valueDown() {
		this.stat.setValue(this.stat.getValue() - 1);
		GmMod.logger().info("-1 " + stat.getName());
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
			drawString(stack, parent.getParent().getFont(), stat.getName(), this.x, this.y + baseY, 16777215);
			drawString(stack, parent.getParent().getFont(), "" + stat.getValue(), this.x + 150, this.y + baseY,
					16777215);

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
//		super.renderToolTip(stack, mouseX, mouseY);
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
