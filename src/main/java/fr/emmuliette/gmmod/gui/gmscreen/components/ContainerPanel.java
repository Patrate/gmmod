package fr.emmuliette.gmmod.gui.gmscreen.components;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerPanel<T> extends ScrollableWidget {
	private final static TextComponent TXT = new TextComponent("");
	List<ScrollableWidget> childrens;

	public ContainerPanel(CustomScrollPanel panel, int ratio) {
		super(panel, 0, 0, 0, 0, ratio, TXT);
		childrens = new ArrayList<ScrollableWidget>();
	}
	
	public void clearChildrens() {
		childrens.clear();
	}
	
	public void addChildren(ScrollableWidget children) {
		childrens.add(children);
	}
	
	public List<ScrollableWidget> getChildrens(){
		return childrens;
	}

	public abstract void clearContent();

	public abstract void updateContent(T sheet);

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	private void drawBackground(int baseY, Tesselator tess) {
		drawBorder(tess, this.x, this.y + baseY, this.width, this.height, CharacterPanel.PADDING,
				CharacterPanel.BORDER);
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
	
	@Override
	public void checkHoveredAndVisible(int mouseX, int mouseY, int baseY) {
		for (ScrollableWidget w : this.getChildrens()) {
			w.checkHoveredAndVisible(mouseX, mouseY, baseY);
		}
		super.checkHoveredAndVisible(mouseX, mouseY, baseY);
	}

	@Override
	public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
		for (ScrollableWidget w : childrens) {
			if (w.mouseClicked(p_93641_, p_93642_, p_93643_))
				return true;
		}
		return super.mouseClicked(p_93641_, p_93642_, p_93643_);
	}

	@Override
	public boolean keyPressed(int p_94710_, int p_94711_, int p_94712_) {
		for (ScrollableWidget w : childrens) {
			if (w.keyPressed(p_94710_, p_94711_, p_94712_))
				return true;
		}
		return super.keyPressed(p_94710_, p_94711_, p_94712_);
	}

	@Override
	public boolean keyReleased(int p_94715_, int p_94716_, int p_94717_) {
		for (ScrollableWidget w : childrens) {
			if (w.keyReleased(p_94715_, p_94716_, p_94717_))
				return true;
		}
		return super.keyReleased(p_94715_, p_94716_, p_94717_);
	}

	@Override
	public boolean charTyped(char p_94683_, int p_94684_) {
		for (ScrollableWidget w : childrens) {
			if (w.charTyped(p_94683_, p_94684_))
				return true;
		}
		return super.charTyped(p_94683_, p_94684_);
	}

}
