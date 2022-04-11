package fr.emmuliette.gmmod.gui.gmscreen.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerPanel extends ScrollableWidget {
	private final static TextComponent TXT = new TextComponent("");

	public ContainerPanel(CharacterPanel panel, int ratio) {
		super(panel, 0, 0, 0, 0, ratio, TXT);
	}

	public abstract void clearContent();

	public abstract void updateContent(CharacterSheet sheet);

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	private void drawBackground(int baseY, Tesselator tess) {
		drawBorder(tess, this.x, this.y + baseY, this.width, this.height, CharacterPanel.PADDING, CharacterPanel.BORDER);
	}

	protected abstract void updateVisible();

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		updateVisible();
		if (this.visible) {
			drawBackground(baseY, tess);
		}
	}

}
