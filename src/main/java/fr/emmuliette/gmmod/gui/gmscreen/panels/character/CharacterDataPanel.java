package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.EditableTextWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.AirWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.ArmorWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.ExperienceWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.FoodWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.HealthWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData.PosWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CharacterDataPanel extends ContainerPanel<CharacterSheet> {
	private LivingEntity entity;
	private EditableTextWidget nameWidget;

	public CharacterDataPanel(CharacterPanel parent, int ratio) {
		super(parent, ratio);
		nameWidget = new EditableTextWidget(parent, 0, 0, 84, 11, new TextComponent(""), (newVal) -> editName(newVal));
		addChildren(nameWidget);
		addChildren(new PosWidget(this));
		addChildren(new HealthWidget(this));
		addChildren(new FoodWidget(this));
		addChildren(new ArmorWidget(this));
		addChildren(new AirWidget(this));
		addChildren(new ExperienceWidget(this));
	}

	private void editName(String newName) {
		if (entity != null && !(entity instanceof Player)) {
			entity.setCustomName(new TextComponent(newName));
			((GmScreen) getParent().getParent()).refreshCharactersList();
		}
	}

	@Override
	public void init() {
		for (ScrollableWidget children : getChildrens()) {
			centerX(children);
		}
	}

	public void clearContent() {
		entity = null;
		nameWidget.setValue("");
	}

	public void updateContent(CharacterSheet sheet) {
		entity = sheet.getOwner();
		nameWidget.setValue(entity.getName().getContents());
		nameWidget.setEditable(!(entity instanceof Player));
		this.height = 60;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
//			Component name = getEntity().getName();
//			Font font = this.getParent().getFont();
//
//			drawString(poseStack, font, name, centerX(this.getParent().getFont().width(name)), this.y + baseY,
//					0xFFFFFFFF);
			int i = 0;
			for (ScrollableWidget w : getChildrens()) {
				if (w.visible) {
					w.y = this.y + 11 * i;
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
	public int getHeight() {
		int newHeight = 16 + CharacterPanel.PADDING * 2 + CharacterPanel.BORDER * 2;
		for (ScrollableWidget w : getChildrens()) {
			if (w.visible)
				newHeight += w.getHeight();
		}
		this.height = newHeight;
		return newHeight;
	}

	@Override
	protected void updateVisible() {
		this.visible = (getEntity() != null);
	}
}
