package fr.emmuliette.gmmod.gui.gmscreen.widgets;

import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;

public interface StatWidgetListener {
	public CustomScrollPanel getParent();

	public void onValueUp(Stat stat);

	public void onValueDown(Stat stat);
}
