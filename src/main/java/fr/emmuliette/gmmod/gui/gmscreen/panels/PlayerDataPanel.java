package fr.emmuliette.gmmod.gui.gmscreen.panels;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData.AirWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData.ArmorWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData.ExperienceWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData.FoodWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData.HealthWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class PlayerDataPanel extends ScrollableWidget {
	private LivingEntity entity;
	private List<ScrollableWidget> childrens;

	public PlayerDataPanel(SheetPanel panel, int x, int y) {
		super(panel, x, y, panel.WIDTH, 0);
		childrens = new ArrayList<ScrollableWidget>();
		childrens.add(new HealthWidget(this, centerX(HealthWidget.WIDTH), this.y + 16));
		childrens.add(new FoodWidget(this, centerX(FoodWidget.WIDTH), this.y + 25));
		childrens.add(new ArmorWidget(this, centerX(ArmorWidget.WIDTH), this.y + 34));
		childrens.add(new AirWidget(this, centerX(AirWidget.WIDTH), this.y + 43));
		childrens.add(new ExperienceWidget(this, centerX(ExperienceWidget.WIDTH), this.y + 52));
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	public void clearContent() {
		entity = null;
	}

	public void updateContent(CharacterSheet sheet) {
		entity = sheet.getOwner();
		this.height = 60;
	}

	private void drawBackground(int baseY, Tesselator tess) {
		drawBorder(tess, this.x, this.y + baseY, getParent().WIDTH, this.height , SheetPanel.PADDING,
				SheetPanel.BORDER);
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible && getEntity() != null) {
			this.drawBackground(baseY, tess);
			Component name = getEntity().getName();
			Font font = this.getParent().getParent().getFontRenderer();

			drawString(poseStack, font, name, centerX(this.getParent().getParent().getFontRenderer().width(name)),
					this.y + baseY, 0xFFFFFFFF);
			int i = 0;
			for (ScrollableWidget w : childrens) {
				if (w.visible) {
					w.y = this.y + 16 + 11 * i;
					w.render(poseStack, mouseX, mouseY, partialTick);
					i++;
				}
			}
		}
	}

	public void bind(ResourceLocation res) {
		RenderSystem.setShaderTexture(0, res);
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setupOverlayRenderState(boolean blend, boolean depthText) {
		setupOverlayRenderState(blend, depthText, Gui.GUI_ICONS_LOCATION);
	}

	public void setupOverlayRenderState(boolean blend, boolean depthTest, @Nullable ResourceLocation texture) {
		if (blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.disableBlend();
		}

		if (depthTest) {
			RenderSystem.enableDepthTest();
		} else {
			RenderSystem.disableDepthTest();
		}

		if (texture != null) {
			RenderSystem.enableTexture();
			bind(texture);
		} else {
			RenderSystem.disableTexture();
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	@Override
	public void checkHoveredAndVisible(int mouseX, int mouseY, int baseY) {
		for (ScrollableWidget w : this.childrens) {
			w.checkHoveredAndVisible(mouseX, mouseY, baseY);
		}
		super.checkHoveredAndVisible(mouseX, mouseY, baseY);
	}

	@Override
	public int getHeight() {
		int newHeight = 16 + SheetPanel.PADDING * 2 + SheetPanel.BORDER * 2;
		for (ScrollableWidget w : childrens) {
			if (w.visible)
				newHeight += w.getHeight();
		}
		this.height = newHeight;
		return newHeight;
	}
}
