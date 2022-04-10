package fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.PlayerDataPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.InternalSWidget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HealthWidget extends InternalSWidget {
	public static final int WIDTH = 84;
	private PlayerDataPanel parent;

	public HealthWidget(PlayerDataPanel parent) {
		super(parent.getParent(), WIDTH, 9, new TextComponent("Health"));
		this.parent = parent;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		parent.setupOverlayRenderState(true, false);
		renderHealth(parent.getEntity(), this.x, this.y + baseY, this.width, this.height, poseStack);
	}

	public void renderHealth(LivingEntity entity, int x, int y, int width, int height, PoseStack pStack) {
		RenderSystem.enableBlend();

		int health = Mth.ceil(entity.getHealth());

		AttributeInstance attrMaxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
		float healthMax = Math.max((float) attrMaxHealth.getValue(), health);
		int absorb = Mth.ceil(entity.getAbsorptionAmount());

		int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);

		this.renderHearts(pStack, entity, x, y, 1, -1, healthMax, health, health, absorb, healthRows > 1);

		RenderSystem.disableBlend();

	}

	protected void renderHearts(PoseStack stack, LivingEntity entity, int left, int top, int rowHeight, int moinsUn,
			float max, int current, int current2, int absorb, boolean overTen) {
		HealthWidget.HeartType gui$hearttype = (entity instanceof Player) ? HeartType.forPlayer((Player) entity)
				: HealthWidget.HeartType.NORMAL;
		int i = 9 * (entity.level.getLevelData().isHardcore() ? 5 : 0);
		if (overTen) {
			this.renderHeart(stack, HealthWidget.HeartType.CONTAINER, left, top, i, false, false);
			this.renderHeart(stack,
					(absorb > 0)
							? (gui$hearttype == HealthWidget.HeartType.WITHERED ? gui$hearttype
									: HealthWidget.HeartType.ABSORBING)
							: gui$hearttype,
					left, top, i, false, false);
			drawString(stack, this.getParent().getFont(), "x" + current, left + 10, top, 0xFFFFFF);
			return;
		}

		int j = Mth.ceil((double) max / 2.0D);
		int k = Mth.ceil((double) absorb / 2.0D);
		int l = j * 2;

		for (int i1 = j + k - 1; i1 >= 0; --i1) {
			int j1 = i1 / 10;
			int k1 = i1 % 10;
			int l1 = left + k1 * 8;
			int i2 = top - j1 * rowHeight;

			if (i1 < j && i1 == moinsUn) {
				i2 -= 2;
			}

			this.renderHeart(stack, HealthWidget.HeartType.CONTAINER, l1, i2, i, false, false);
			int j2 = i1 * 2;
			boolean flag = i1 >= j;
			if (flag) {
				int k2 = j2 - l;
				if (k2 < absorb) {
					boolean flag1 = k2 + 1 == absorb;
					this.renderHeart(stack, gui$hearttype == HealthWidget.HeartType.WITHERED ? gui$hearttype
							: HealthWidget.HeartType.ABSORBING, l1, i2, i, false, flag1);
				}
			}

			if (overTen && j2 < current2) {
				boolean flag2 = j2 + 1 == current2;
				this.renderHeart(stack, gui$hearttype, l1, i2, i, true, flag2);
			}

			if (j2 < current) {
				boolean flag3 = j2 + 1 == current;
				this.renderHeart(stack, gui$hearttype, l1, i2, i, false, flag3);
			}
		}
	}

	private void renderHeart(PoseStack p_168701_, HealthWidget.HeartType type, int p_168703_, int p_168704_,
			int p_168705_, boolean p_168706_, boolean p_168707_) {
		this.blit(p_168701_, p_168703_, p_168704_, type.getX(p_168707_, p_168706_), p_168705_, 9, 9);
	}

	@OnlyIn(Dist.CLIENT)
	static enum HeartType {
		CONTAINER(0, false), NORMAL(2, true), POISIONED(4, true), WITHERED(6, true), ABSORBING(8, false),
		FROZEN(9, false);

		private final int index;
		private final boolean canBlink;

		private HeartType(int p_168729_, boolean p_168730_) {
			this.index = p_168729_;
			this.canBlink = p_168730_;
		}

		public int getX(boolean p_168735_, boolean p_168736_) {
			int i;
			if (this == CONTAINER) {
				i = p_168736_ ? 1 : 0;
			} else {
				int j = p_168735_ ? 1 : 0;
				int k = this.canBlink && p_168736_ ? 2 : 0;
				i = j + k;
			}

			return 16 + (this.index * 2 + i) * 9;
		}

		static HealthWidget.HeartType forPlayer(Player p_168733_) {
			HealthWidget.HeartType gui$hearttype;
			if (p_168733_.hasEffect(MobEffects.POISON)) {
				gui$hearttype = POISIONED;
			} else if (p_168733_.hasEffect(MobEffects.WITHER)) {
				gui$hearttype = WITHERED;
			} else if (p_168733_.isFullyFrozen()) {
				gui$hearttype = FROZEN;
			} else {
				gui$hearttype = NORMAL;
			}

			return gui$hearttype;
		}
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = (parent.getEntity() != null && (parent.getEntity() instanceof Player)
				&& !((Player) parent.getEntity()).isCreative());
	}
}
