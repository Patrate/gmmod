package fr.emmuliette.gmmod.gui.gmscreen.panels.jobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.characterSheet.stats.DummyStat;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.StatWidgetListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatBlockPanel extends ContainerPanel<JobTemplate> implements StatWidgetListener {
	private JobTemplate template;

	public StatBlockPanel(JobsPanel parent, int ratio) {
		super(parent, ratio);
	}

	public void clearContent() {
		clearChildrens();
		this.template = null;
	}

	public void updateContent(JobTemplate template) {
		clearChildrens();
		this.template = template;
		int i = 0;
		int biggest = 0;
		for (Class<? extends Stat> statClass : Stat.getAllStats()) {
			Stat dStat = new DummyStat(statClass);
			dStat.setBaseValue(template.getStatBlock().getStatPerLevel(statClass));
			StatWidget w = new StatWidget(this, 0, 2 + this.y + i * 20, dStat);
			addChildren(w);
			biggest = (w.getTextWidth() > biggest) ? w.getTextWidth() : biggest;
			i++;
		}
		int newX = centerX(biggest + StatWidget.PADDING_TOTAL);
		for (ScrollableWidget w : getChildrens()) {
			w.x = newX;
			((StatWidget) w).setTextWidth(biggest);
		}
		this.height = i * 20 + 2 * 2 + 2 * 2;
	}

	@Override
	public int setScrollY(int newY) {
		int diff = super.setScrollY(newY);
		if (diff == 0)
			return 0;
		for (ScrollableWidget w : getChildrens()) {
			w.y += diff;
		}
		return diff;
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
			for (ScrollableWidget w : getChildrens()) {
				w.render(poseStack, mouseX, mouseY, partialTick);
			}
		}
	}

	@Override
	protected void updateVisible() {
		this.visible = !getChildrens().isEmpty();
	}

	@Override
	public void init() {
		for (ScrollableWidget w : getChildrens()) {
			centerX(w);
		}
	}

	@Override
	public void onValueUp(Stat stat) {
		stat.setBaseValue(stat.getBaseValue() + 1);
		template.getStatBlock().setStatPerLevel(stat.getClass(), stat.getBaseValue());
	}

	@Override
	public void onValueDown(Stat stat) {
		stat.setBaseValue(stat.getBaseValue() - 1);
		template.getStatBlock().setStatPerLevel(stat.getClass(), stat.getBaseValue());
	}

	public void syncToTemplate(Stat stat) {

	}
}
