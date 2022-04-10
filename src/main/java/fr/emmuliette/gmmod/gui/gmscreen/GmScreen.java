package fr.emmuliette.gmmod.gui.gmscreen;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.panels.SheetPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.PlayerListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;

@OnlyIn(Dist.CLIENT)
public class GmScreen extends Screen {

	private PlayerListWidget playerList;
	private SheetPanel sheetPanel;

	public GmScreen() {
		super(new TextComponent("Gm screen"));
		if (this.minecraft == null)
			this.minecraft = Minecraft.getInstance();

	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	public boolean isOpen() {
		return this.equals(this.minecraft.screen);
	}

	public void open() {
		GmMod.logger().debug("OPENING GM SCREEN");
		this.minecraft.setScreen(this);
	}

	public void close() {
		GmMod.logger().debug("CLOSING GM SCREEN");
		this.minecraft.setScreen(null);
	}

	@Override
	protected void init() {
		this.playerList = new PlayerListWidget(this, 80, 20);
		this.addRenderableWidget(playerList);
		this.sheetPanel = new SheetPanel(minecraft, this, (width - playerList.getRight()), height, 0,
				playerList.getRight());
		this.addRenderableWidget(sheetPanel);
	}

	public void selectPlayer(String playerName) {
		ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(playerName);
		if (player == null)
			return;
		CharacterSheet sheet = player.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
		if (sheet == null)
			return;

		sheetPanel.setInfo(sheet);
	}

	public Font getFontRenderer() {
		return font;
	}
}
