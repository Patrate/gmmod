package fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.PlayerDataPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class FoodWidget extends ScrollableWidget {
	public static final int WIDTH = 84;
	private PlayerDataPanel parent;

	public FoodWidget(PlayerDataPanel parent, int x, int y) {
		super(parent.getParent(), x, y, WIDTH, 9, new TextComponent("Food"));
		this.parent = parent;
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		parent.setupOverlayRenderState(true, false);
		renderFood(parent.getEntity(), this.x, this.y + baseY, poseStack);
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
		visible = (parent.getEntity() != null && (parent.getEntity() instanceof Player) && !((Player) parent.getEntity()).isCreative());
	}
}
