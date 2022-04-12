package fr.emmuliette.gmmod.gui.gmscreen.widgets.characterData;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterDataPanel;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;

public class PosWidget extends InternalSWidget {
	public static final int WIDTH = 84;
	private CharacterDataPanel parent;

	public PosWidget(CharacterDataPanel parent) {
		super(parent.getParent(), WIDTH, 9, new TextComponent("Pos"));
		this.parent = parent;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		BlockPos blockPos = parent.getEntity().blockPosition();
		String pos = blockPos.toShortString();
		String dim = parent.getEntity().level.dimension().location().toString();
		Font font = this.getParent().getFont();

		drawCenteredString(poseStack, font, dim + "   " + pos, this.x + this.width / 2, this.y + baseY, 0xFFFFFFFF);
	}

	@Override
	protected void checkVisible(int mouseX, int mouseY, int baseY) {
		visible = parent.getEntity() != null;
	}
}
