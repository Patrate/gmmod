package fr.emmuliette.gmmod.gui.gmscreen.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends Button {
	private TabsWidget parent;
	private int id;
	private final ResourceLocation resourceLocation;
	private final int xTexStart;
	private final int yTexStart;
	private final int textureWidth;
	private final int textureHeight;

	public TabButton(TabsWidget parent, int id, int x, int y, int xTexStart, int yTexStart, int textureWidth,
			int textureHeight, ResourceLocation icon, String translationKey) {
		super(x, y, TABWIDTH, TABHEIGHT, new TranslatableComponent(translationKey), TabButton::onPress);
		this.parent = parent;
		this.id = id;
		this.resourceLocation = icon;
		this.xTexStart = xTexStart;
		this.yTexStart = yTexStart;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		if (visible) {
			renderTab(pose);
			super.render(pose, mouseX, mouseY, partialTick);
		}
	}

	public static void onPress(Button b) {
		TabButton button = (TabButton) b;
		button.parent.select(button.id);
	}

	public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, this.resourceLocation);
		int i = this.x + 3;
		int j = this.y + 8;
		if (this.id == parent.getCurrentTab()) {
			j -= 4;
		}

		RenderSystem.enableDepthTest();
		blit(stack, i, j, (float) this.xTexStart, (float) this.yTexStart, this.textureWidth, this.textureHeight, 256,
				256);
		if (this.isHovered) {
			this.renderToolTip(stack, mouseX, mouseY);
		}

	}

	static final int TABWIDTH = 20;
	static final int TABHEIGHT = 20;

	public void renderTab(PoseStack pose) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, TabsWidget.TABS_RESOURCES);
		int i = 0;
		if (this.id == parent.getCurrentTab()) {
			i += TABHEIGHT;
		}

		RenderSystem.enableDepthTest();
		blit(pose, this.x, this.y, 0f, (float) i, TABWIDTH, TABHEIGHT, 256, 256);
	}

}
