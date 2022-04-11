package fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.PlayerDataPanel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class FoodWidget extends InternalSWidget {
	public static final int WIDTH = 84;
	private PlayerDataPanel parent;

	public FoodWidget(PlayerDataPanel parent) {
		super(parent.getParent(), WIDTH, 9, new TextComponent("Food"));
		this.parent = parent;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		parent.setupOverlayRenderState(true, false);
		renderFood(parent.getEntity(), this.x, this.y + baseY, poseStack);
		if (this.isHovered)
			renderToolTip(poseStack, mouseX, mouseY, (Player) parent.getEntity());
	}

	public void renderToolTip(PoseStack stack, int mouseX, int mouseY, Player player) {
		drawString(stack, parent.getParent().getFont(),
				new TextComponent("Saturation " + player.getFoodData().getSaturationLevel()), mouseX, mouseY, 16777215);
	}

	public void renderFood(LivingEntity entity, int left, int top, PoseStack poseStack) {
		if (!(entity instanceof Player))
			return;
		Player player = (Player) entity;
		RenderSystem.enableBlend();

		FoodData stats = player.getFoodData();
		int level = stats.getFoodLevel();

		for (int i = 9; i >= 0; --i) {
			int idx = i * 2 + 1;
			int x = left + i * 8;
			int y = top;
			int icon = 16;
			byte background = 0;

			if (player.hasEffect(MobEffects.HUNGER)) {
				icon += 36;
				background = 13;
			}

			blit(poseStack, x, y, 16 + background * 9, 27, 9, 9);

			if (idx < level)
				blit(poseStack, x, y, icon + 36, 27, 9, 9);
			else if (idx == level)
				blit(poseStack, x, y, icon + 45, 27, 9, 9);
		}
		RenderSystem.disableBlend();
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = (parent.getEntity() != null && (parent.getEntity() instanceof Player)
				&& !((Player) parent.getEntity()).isCreative());
	}
}
