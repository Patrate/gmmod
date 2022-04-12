package fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterDataPanel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;

public class ArmorWidget extends InternalSWidget {
	public static final int WIDTH = 84;
	private CharacterDataPanel parent;

	public ArmorWidget(CharacterDataPanel parent) {
		super(parent.getParent(), WIDTH, 9, new TextComponent("Armor"));
		this.parent = parent;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		parent.setupOverlayRenderState(true, false);
		renderArmor(parent.getEntity(), this.x, this.y + baseY, poseStack);
	}

	protected void renderArmor(LivingEntity entity, int left, int top, PoseStack poseStack) {
		RenderSystem.enableBlend();

		int level = entity.getArmorValue();
		for (int i = 1; level > 0 && i < 20; i += 2) {
			if (i < level) {
				blit(poseStack, left, top, 34, 9, 9, 9);
			} else if (i == level) {
				blit(poseStack, left, top, 25, 9, 9, 9);
			} else if (i > level) {
				blit(poseStack, left, top, 16, 9, 9, 9);
			}
			left += 8;
		}

		RenderSystem.disableBlend();
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = parent.getEntity() != null && parent.getEntity().getArmorValue() > 0;
	}
}
