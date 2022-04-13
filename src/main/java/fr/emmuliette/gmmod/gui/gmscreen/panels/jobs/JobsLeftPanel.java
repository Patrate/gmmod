package fr.emmuliette.gmmod.gui.gmscreen.panels.jobs;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@OnlyIn(Dist.CLIENT)
public class JobsLeftPanel extends ObjectSelectionList<JobsLeftPanel.JobTemplateEntry> {
	private Font font;
	private boolean visible;
	private GmScreen parent;
	

	public JobsLeftPanel(GmScreen parent, int width, int topPadding, int bottomPadding) {
		super(parent.getMinecraft(), width, parent.height, topPadding, parent.height - bottomPadding,
				parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.visible = true;
		this.font = parent.getFontRenderer();
		this.setRenderBackground(false);
		this.refreshList();
	}

	@Override
	protected int getScrollbarPosition() {
		return this.width;
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	public void refreshList() {
		this.clearEntries();
		for (String j : JobTemplate.getJobsList()) {
			this.addEntry(new JobTemplateEntry(j));
		}
	}

	public class JobTemplateEntry extends ObjectSelectionList.Entry<JobTemplateEntry> {
		private final String name;

		JobTemplateEntry(String name) {
			this.name = name;
		}

		@Override
		public Component getNarration() {
			return new TranslatableComponent("narrator.select", name);
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
			font.draw(poseStack, new TextComponent(name), left + 3, top + 2, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.equals(getSelected()))
				return false;
			setSelected(this);
			return true;
		}

		public String getTemplateName() {
			return name;
		}

		public JobTemplate getTemplate() {
			return JobTemplate.getTemplate(name);
		}
	}

	@Override
	public void setSelected(JobTemplateEntry j) {
		super.setSelected(j);
		parent.loadJobTemplate(j.getTemplate());
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (this.visible)
			super.render(stack, mouseX, mouseY, partialTick);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
