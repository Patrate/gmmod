package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;
import fr.emmuliette.gmmod.gui.gmscreen.components.InternalSWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditableTextWidget extends InternalSWidget implements Tickable {
	private EditBox searchBox;
	private boolean ignoreTextInput;
	private CustomScrollPanel parent;
	private OnChange onChange;
	private boolean editable;

	private Component defaultValue;

	@SuppressWarnings("resource")
	public EditableTextWidget(CustomScrollPanel parent, int x, int y, int width, int height, Component defaultValue,
			OnChange onchange) {
		super(parent, x, y, width, height, defaultValue);
		this.parent = parent;
		parent.getParent().getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
		this.defaultValue = defaultValue;
		this.onChange = onchange;
		this.editable = true;
		((GmScreen) parent.getParent()).registerTickable(this);
		init();
	}

	public String getValue() {
		return searchBox.getValue();
	}

	public void setValue(String content) {
		searchBox.setValue(content);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable() {
		return this.editable;
	}

	@Override
	public void init() {
		String s = this.searchBox != null ? this.searchBox.getValue() : "";
		this.searchBox = new EditBox(parent.getFont(), x, y, width, height, defaultValue);
		this.searchBox.setMaxLength(50);
		this.searchBox.setBordered(false);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(16777215);
		this.searchBox.setValue(s);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!editable)
			return false;
		double newY = mouseY + parent.getScrollData().baseY;
		if (this.searchBox.mouseClicked(mouseX, newY, button))
			return true;
		return false;
	}

	@Override
	protected void setX(int newX) {
		super.setX(newX);
		this.searchBox.x = newX;
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		if (this.visible) {
			if (editable) {
//				if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
//					drawString(stack, parent.getFont(), "_", x, y + baseY, -1);
//				} else {
				this.searchBox.x = centerX(this.getParent().getFont().width(searchBox.getValue()));
				this.searchBox.y = this.y + baseY;
				this.searchBox.render(stack, mouseX, mouseY, partialTick);
//				}
			} else {
				drawString(stack, getParent().getFont(), searchBox.getValue(),
						centerX(this.getParent().getFont().width(searchBox.getValue())), y + baseY, 0xFFFFFFFF);
			}
			if (this.isHovered)
				renderToolTip(stack, mouseX, mouseY);
		}
	}

	@Override
	public void tick() {
		if (editable)
			this.searchBox.tick();
	}

	public boolean keyPressed(int p_100306_, int p_100307_, int p_100308_) {
		if (!editable)
			return false;
		this.ignoreTextInput = false;
		if (this.searchBox.keyPressed(p_100306_, p_100307_, p_100308_)) {
			onChange.onChange(this.searchBox.getValue());
			return true;
		} else if (this.searchBox.isFocused() && this.searchBox.isVisible() && p_100306_ != 256) {
			return true;
		} else if (parent.getParent().getMinecraft().options.keyChat.matches(p_100306_, p_100307_)
				&& !this.searchBox.isFocused()) {
			this.ignoreTextInput = true;
			this.searchBox.setFocus(true);
			return true;
		} else {
			return false;
		}
	}

	public boolean charTyped(char p_100291_, int p_100292_) {
		if (!editable)
			return false;
		if (this.ignoreTextInput) {
			return false;
		} else if (this.visible) {
			if (this.searchBox.charTyped(p_100291_, p_100292_)) {
				onChange.onChange(this.searchBox.getValue());
				return true;
			}
		}
		return false;
	}

	public interface OnChange {
		public void onChange(String newValue);
	}
}
