package fr.emmuliette.gmmod.gui.gmscreen.components;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TabsWidget extends AbstractWidget {
	static final ResourceLocation TABS_RESOURCES = new ResourceLocation(GmMod.MOD_ID, "textures/gui/gmscreen.png");
	private int currentTab;
	private List<Button> tabs;
	private TabSelectorListener parent;

	public TabsWidget(GmScreen parent, int x, int y, int width, int height) {
		super(x, y, width, height, new TextComponent("tabs"));
		tabs = new ArrayList<Button>();
		this.parent = parent;
		this.currentTab = 0;
	}

	public void addTab(ResourceLocation icon, int xTexStart, int yTexStart, int textureWidth, int textureHeight,
			String name) {
		final int id = tabs.size();
		Button newButton = new TabButton(this, id, this.x + id * TabButton.TABWIDTH, this.y, xTexStart, yTexStart, textureWidth,
				textureHeight, icon, name);
		tabs.add(newButton);
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (this.visible) {
			for (Button b : tabs) {
				b.render(stack, mouseX, mouseY, partialTick);
			}
		}
	}

	void select(int id) {
		this.currentTab = id;
		parent.selectTab(id);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (Button b : tabs) {
			if (b.mouseClicked(mouseX, mouseY, button))
				return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	int getCurrentTab() {
		return currentTab;
	}

}
