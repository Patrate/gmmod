package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

@OnlyIn(Dist.CLIENT)
public class PlayerListWidget extends ObjectSelectionList<PlayerListWidget.PlayerEntry> {
	private final int listWidth;
	private List<String> playersNames;

	private GmScreen parent;

	public PlayerListWidget(GmScreen parent, int width, int verticalPadding) {
		super(parent.getMinecraft(), width, parent.height, verticalPadding, parent.height - verticalPadding,
				parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.listWidth = width;
		this.playersNames = Collections
				.unmodifiableList(Arrays.asList(ServerLifecycleHooks.getCurrentServer().getPlayerNames()));
		this.setRenderBackground(false);
		this.refreshList();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.listWidth;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

	public void refreshList() {
		this.clearEntries();
		for (String pName : playersNames) {
			this.addEntry(new PlayerEntry(pName, parent));
		}
	}

	@Override
	protected void renderBackground(PoseStack poseStack) {
		this.parent.renderBackground(poseStack);
	}

	public class PlayerEntry extends ObjectSelectionList.Entry<PlayerEntry> {
		private final String playerName;
		private final GmScreen parent;

		PlayerEntry(String playerName, GmScreen parent) {
			this.playerName = playerName;
			this.parent = parent;
		}

		@Override
		public Component getNarration() {
			return new TranslatableComponent("narrator.select", playerName);
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
			Font font = this.parent.getFontRenderer();
			font.draw(poseStack, new TextComponent(playerName), left + 3, top + 2, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.equals(getSelected()))
				return false;
			setSelected(this);
			return true;
		}

		public String getPlayer() {
			return playerName;
		}
	}

	@Override
	public void setSelected(PlayerEntry p) {

		super.setSelected(p);
		parent.selectPlayer(p.playerName);
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		super.render(stack, mouseX, mouseY, partialTick);
	}
}
