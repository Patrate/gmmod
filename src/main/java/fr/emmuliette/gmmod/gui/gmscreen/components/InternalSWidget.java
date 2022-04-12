package fr.emmuliette.gmmod.gui.gmscreen.components;

import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class InternalSWidget extends ScrollableWidget {

	public InternalSWidget(CustomScrollPanel panel, int width, int height, Component message) {
		super(panel, 0, 0, width, height, message);
	}

	public InternalSWidget(CustomScrollPanel panel, int x, int y, int width, int height, Component message) {
		super(panel, x, y, width, height, message);
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

}
