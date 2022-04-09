package fr.emmuliette.gmmod.gui.gmscreen.panels;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraftforge.client.gui.ScrollPanel;

public class SheetPanel extends ScrollPanel {
	public final static int PADDING = 2, BORDER = 2, BAR_SIZE = 6;
	public final int WIDTH;
	private List<ScrollableWidget> childrens;
	private final GmScreen parent;
	private ScrollData scrollData;
	private float partialTick;

	private StatPanel stats;
	private PlayerDataPanel playerData;

	public SheetPanel(Minecraft mcIn, GmScreen parent, int widthIn, int heightIn, int top, int left) {
		super(mcIn, widthIn, heightIn, top, left, 0, BAR_SIZE, 0X00FFFFFF, 0x00FFFFFF);// -1072689136,
																												// -804253680);
		this.parent = parent;
		this.childrens = new ArrayList<ScrollableWidget>();
		WIDTH = widthIn - (PADDING + BORDER) * 2 - BAR_SIZE; // * 2 parce que y'a la droite ET la gauche con de toi :B
		init();
	}

	private void init() {
		this.playerData = new PlayerDataPanel(this, this.left + PADDING + BORDER,
				this.top);
		childrens.add(playerData);
		this.stats = new StatPanel(this, this.left + PADDING + BORDER, this.top);
		childrens.add(stats);
		getContentHeight();
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return childrens;
	}

	public GmScreen getParent() {
		return parent;
	}

	public void setInfo(CharacterSheet sheet) {
		this.stats.updateContent(sheet);
		this.playerData.updateContent(sheet);
		getContentHeight();
	}

	void clearInfo() {
		this.stats.clearContent();
		this.playerData.clearContent();
	}

	@Override
	public int getContentHeight() {
		int contentHeight = 0;
		for (ScrollableWidget children : childrens) {
			children.setScrollY(this.top + PADDING + BORDER + contentHeight);
			contentHeight += children.getHeight() + PADDING + BORDER;
		}
		contentHeight += PADDING + BORDER;
		if (contentHeight < this.bottom - this.top - (PADDING + BORDER))
			contentHeight = this.bottom - this.top - (PADDING + BORDER);
		return contentHeight;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
		this.partialTick = partialTick;
		super.render(matrix, mouseX, mouseY, partialTick);
	}

	@Override
	protected void drawPanel(PoseStack poseStack, int entryRight, int relativeY, Tesselator tess, int mouseX,
			int mouseY) {
		this.scrollData = new ScrollData(entryRight, relativeY, tess);
		for (ScrollableWidget w : childrens) {
			w.checkHoveredAndVisible(mouseX, mouseY, relativeY);
			w.render(poseStack, mouseX, mouseY, partialTick);
		}
	}

	public ScrollData getScrollData() {
		return scrollData;
	}

	@Override
	public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
		double newMouseY = (this.scrollData == null) ? mouseY : (mouseY - this.getScrollData().baseY);
		for (ScrollableWidget w : this.childrens) {
			if (w.mouseClicked(mouseX, newMouseY, button))
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.HOVERED;
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	public class ScrollData {
		public final int right, baseY;
		public final Tesselator tess;

		public ScrollData(int right, int baseY, Tesselator tess) {
			this.right = right;
			this.baseY = baseY;
			this.tess = tess;
		}
	}
}