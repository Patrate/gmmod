package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel.ScrollData;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public abstract class ScrollableWidget extends AbstractWidget {
	private CustomScrollPanel parent;
	private int ratio;

	public ScrollableWidget(CustomScrollPanel panel, int x, int y, int width, int height, Component message) {
		this(panel, x, y, width, height, width, message);
	}

	public ScrollableWidget(CustomScrollPanel panel, int x, int y, int width, int height, int ratio,
			Component message) {
		super(x, y, width, height, message);
		parent = panel;
		this.ratio = ratio;
		if (ratio <= 0)
			// TODO throw error
			return;
	}

	public abstract void init();

	protected abstract void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight,
			int baseY, Tesselator tess);

	@Override
	public final void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		if (this.getParent() == null)
			return;
		ScrollData data = this.getParent().getScrollData();
		render(poseStack, mouseX, mouseY, partialTick, data.right, data.baseY, data.tess);
	}

	protected void setX(int newX) {
		this.x = newX;
	}

	protected void centerX(ScrollableWidget widget) {
		widget.setX(centerX(widget.width));
	}

	protected int centerX(int widgetWidth) {
		return this.x + (this.width / 2 - widgetWidth / 2);
	}

	public CustomScrollPanel getParent() {
		return parent;
	}

	public void checkHoveredAndVisible(int mouseX, int mouseY, int baseY) {
		int newY = this.y + baseY;
		this.isHovered = mouseX >= this.x && mouseY >= newY && mouseX < this.x + this.width
				&& mouseY < newY + this.height;
		checkVisible(mouseX, mouseY, baseY);
	}

	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = true;
	}

	public int setScrollY(int newY) {
		int diff = newY - this.y;
		this.y = newY;
		return diff;
	}

	public static void drawBorder(Tesselator tess, int x, int y, int width, int height, int padding, int borderSize) {
		vertexifie(tess, x, y, width, height, padding + borderSize, 0xFF, 0xFF, 0xFF, 0xFF);
		vertexifie(tess, x, y, width, height, padding, 0x11, 0x11, 0x11, 0xFF);
	}

	private static void vertexifie(Tesselator tess, int x, int y, int width, int height, int padding, int r, int g,
			int b, int alpha) {
		BufferBuilder worldr = tess.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		final float texScale = 64.0F;
		worldr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		worldr.vertex(x - padding, y + height + padding, 0.0D)
				.uv((x - padding) / texScale, (y + height + padding) / texScale).color(r, g, b, alpha).endVertex();
		worldr.vertex(x + width + padding, y + height + padding, 0.0D)
				.uv((x + width + padding) / texScale, (y + height + padding) / texScale).color(r, g, b, alpha)
				.endVertex();
		worldr.vertex(x + width + padding, y - padding, 0.0D)
				.uv((x + width + padding) / texScale, (y - padding) / texScale).color(r, g, b, alpha).endVertex();
		worldr.vertex(x - padding, y - padding, 0.0D).uv((x - padding) / texScale, (y - padding) / texScale)
				.color(r, g, b, alpha).endVertex();
		tess.end();
	}

	public int getRatio() {
		return ratio;
	}
}
