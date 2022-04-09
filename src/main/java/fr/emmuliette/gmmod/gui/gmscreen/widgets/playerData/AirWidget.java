package fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.PlayerDataPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class AirWidget extends ScrollableWidget {
	public static final int WIDTH = 84;
	private PlayerDataPanel parent;

	public AirWidget(PlayerDataPanel parent, int x, int y) {
		super(parent.getParent(), x, y, WIDTH, 9, new TextComponent("Air"));
		this.parent = parent;
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		parent.setupOverlayRenderState(true, false);
		renderAir(parent.getEntity(), this.x, this.y + baseY, poseStack);
	}

	protected void renderAir(LivingEntity entity, int left, int top, PoseStack poseStack) {
		RenderSystem.enableBlend();

		int air = entity.getAirSupply();
		int maxAir = entity.getMaxAirSupply();
		if (entity.isEyeInFluid(FluidTags.WATER) || air < maxAir) {
			int full = Mth.ceil((double) (air - 2) * 10.0D / (double) maxAir);
			int partial = Mth.ceil((double) air * 10.0D / (double) maxAir) - full;

			for (int i = full + partial - 1; i >= 0; --i) {
				blit(poseStack, left + i * 8, top, (i < full ? 16 : 25), 18, 9, 9);
			}
		}
		RenderSystem.disableBlend();
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = parent.getEntity() != null && (parent.getEntity().isEyeInFluid(FluidTags.WATER)
				|| parent.getEntity().getAirSupply() < parent.getEntity().getMaxAirSupply());
	}
}
