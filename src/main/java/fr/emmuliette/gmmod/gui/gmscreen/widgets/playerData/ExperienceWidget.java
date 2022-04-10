package fr.emmuliette.gmmod.gui.gmscreen.widgets.playerData;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.PlayerDataPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.InternalSWidget;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class ExperienceWidget extends InternalSWidget {
	public static final int WIDTH = 88;
	private PlayerDataPanel parent;

	public ExperienceWidget(PlayerDataPanel parent) {
		super(parent.getParent(), WIDTH, 16, new TextComponent("Experience"));
		this.parent = parent;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (!(parent.getEntity() instanceof Player))
			return;
		parent.setupOverlayRenderState(false, false);
		renderExperience((Player) parent.getEntity(), this.x, this.y + baseY + 3, poseStack);
		if (this.isHovered)
			renderToolTip(poseStack, mouseX, mouseY, (Player) parent.getEntity());
	}

	public void renderToolTip(PoseStack stack, int mouseX, int mouseY, Player player) {
		drawString(stack, parent.getParent().getFont(),
				new TextComponent("" + (int) player.experienceProgress + "/" + player.getXpNeededForNextLevel()),
				mouseX, mouseY, 16777215);
	}

	@SuppressWarnings("resource")
	protected void renderExperience(Player player, int x, int y, PoseStack poseStack) {
		if (visible) {
			RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
			int xpForNextLevel = player.getXpNeededForNextLevel();
			if (xpForNextLevel > 0) {
				int filledPart = (int) (player.experienceProgress * this.width);
				this.blit(poseStack, x, y, 0, 64, this.width, 5);
				if (filledPart > 0) {
					this.blit(poseStack, x, y, 0, 69, filledPart, 5);
				}
			}

			String s = "" + player.experienceLevel;
			int xPos = x + (this.width - this.getParent().getFont().width(s)) / 2;
			int yPos = y - 3;
			this.getParent().getFont().draw(poseStack, s, (float) (xPos + 1), (float) yPos, 0);
			this.getParent().getFont().draw(poseStack, s, (float) (xPos - 1), (float) yPos, 0);
			this.getParent().getFont().draw(poseStack, s, (float) xPos, (float) (yPos + 1), 0);
			this.getParent().getFont().draw(poseStack, s, (float) xPos, (float) (yPos - 1), 0);
			this.getParent().getFont().draw(poseStack, s, (float) xPos, (float) yPos, 8453920);
		}
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@SuppressWarnings("resource")
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = (parent.getEntity() != null && parent.getEntity() instanceof Player
				&& getParent().getParent().getMinecraft().gameMode.hasExperience());
	}

}
