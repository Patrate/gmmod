package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.SheetPanel;
import fr.emmuliette.gmmod.gui.gmscreen.panels.SheetPanel.ScrollData;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ScrollButton extends Button {
	private SheetPanel parent;

	public ScrollButton(SheetPanel parent, int x, int y, int width, int height, Component title, OnPress onPress) {
		super(x, y, width, height, title, onPress);
		this.parent = parent;
	}

	@Override
	public final void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (parent == null)
			return;
		ScrollData data = parent.getScrollData();
		render(poseStack, mouseX, mouseY, partialTick, data.right, data.baseY, data.tess);
	}

	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible) {
			this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
					&& mouseY < this.y + this.height;
			this.renderButton(poseStack, mouseX, mouseY, partialTick);
		}
	}

}
