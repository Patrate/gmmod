package fr.emmuliette.gmmod.gui.gmscreen.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScrollPanel;

@OnlyIn(Dist.CLIENT)
public abstract class CustomScrollPanel extends ScrollPanel {
	private int padding, border, bar_size;
	private SortedMap<Integer, List<ScrollableWidget>> childrens;
	private ScrollData scrollData;
	private float partialTick;
	private Screen parent;

	public CustomScrollPanel(Minecraft mc, Screen parent, int width, int height, int top, int left, int padding,
			int border, int bar_size) {
		super(mc, width, height, top, left, 0, bar_size, 0X00FFFFFF, 0x00FFFFFF);
		this.parent = parent;
		this.childrens = new TreeMap<Integer, List<ScrollableWidget>>();
		this.padding = padding;
		this.border = border;
		this.bar_size = bar_size;
		_init();
	}

	private final void _init() {
		init();
		setContentWidth();
		getContentHeight();
	}

	private void setContentWidth() {
		for (Integer row : childrens.keySet()) {
			List<ScrollableWidget> rowChildrens = childrens.get(row);
			if (rowChildrens.size() == 1) {
				ScrollableWidget children = rowChildrens.get(0);
				children.x = this.left + padding + border;
				children.setWidth(this.width - bar_size - padding - border - 4);
				children.init();
				continue;
			}
			int totalRatio = 0;
			for (ScrollableWidget children : rowChildrens) {
				totalRatio += children.getRatio();
			}
			if (totalRatio == 0)
				// TODO throw error
				return;
			int step = (this.width - bar_size - 4) / totalRatio - padding - border;
			int reste = Math.max(0, (this.width - bar_size - 4) - ((step + padding * 2 + border) * totalRatio));
			int nextX = this.left + padding + border;
			for (int i = 0; i < rowChildrens.size(); i++) {
				boolean last = (i == rowChildrens.size() - 1);
				ScrollableWidget children = rowChildrens.get(i);
				int size = (children.getRatio() * step) + (last ? reste : 0);
				children.x = nextX;
				children.setWidth(size);
				nextX += size + padding + border;
				children.init();
			}
		}
	}

	protected abstract void init();

	public void addWidget(ScrollableWidget w, int row) {
		if (!childrens.containsKey(row))
			childrens.put(row, new ArrayList<ScrollableWidget>());
		childrens.get(row).add(w);
	}

	public List<ScrollableWidget> widgets() {
		List<ScrollableWidget> retour = new ArrayList<ScrollableWidget>();
		for (Integer row : childrens.keySet()) {
			retour.addAll(childrens.get(row));
		}
		return retour;
	}

	@Override
	public List<? extends GuiEventListener> children() {
		return (List<? extends GuiEventListener>) widgets();
	}

	public Screen getParent() {
		return parent;
	}

	@Override
	public int getContentHeight() {
		int contentHeight = 0;
		int biggest = 0;
		for (Integer row : childrens.keySet()) {
			for (ScrollableWidget w : childrens.get(row)) {
				w.setScrollY(this.top + padding + border + contentHeight);
				int wHeight = w.getHeight();
				if (wHeight > biggest)
					biggest = wHeight;
			}
			for (ScrollableWidget w : childrens.get(row)) {
				w.setHeight(biggest);
			}
			contentHeight += biggest + padding + border;
		}
		contentHeight += padding + border;
		if (contentHeight < this.bottom - this.top - (padding + border))
			contentHeight = this.bottom - this.top - (padding + border);
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
		for (ScrollableWidget w : widgets()) {
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
		for (ScrollableWidget w : widgets()) {
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

	public abstract Font getFont();
}