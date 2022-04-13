package fr.emmuliette.gmmod.gui.gmscreen.panels.jobs;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.jobs.JobTemplate;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import fr.emmuliette.gmmod.gui.gmscreen.components.CustomScrollPanel;
import fr.emmuliette.gmmod.gui.gmscreen.components.ScrollButton;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterPanel;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.EditableTextWidget;
import fr.emmuliette.gmmod.gui.gmscreen.widgets.ScrollableWidget;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JobDataPanel extends ContainerPanel<JobTemplate> {
	private JobsPanel parent;
	private JobTemplate template;
	private EditableTextWidget nameWidget;

	public JobDataPanel(JobsPanel parent, int ratio) {
		super(parent, ratio);
		this.parent = parent;
		nameWidget = new EditableTextWidget(parent, 0, 0, 84, 11, new TextComponent(""), (newVal) -> editName(newVal));
		addChildren(nameWidget);
		addChildren(new InternalButton(parent, 0, 0, 52, 24));
	}

	@Override
	protected void setX(int newX) {
		super.setX(newX);
		nameWidget.x = newX;
	}

	class InternalButton extends ScrollableWidget {
		private ScrollButton saveButton, deleteButton;

		public InternalButton(CustomScrollPanel panel, int x, int y, int width, int height) {
			super(panel, x, y, width, height, new TextComponent(""));
			this.saveButton = new ScrollButton(panel, x, y, 24, 16, new TextComponent("save"), button -> save());
			this.deleteButton = new ScrollButton(panel, x + 28, y, 24, 16, new TextComponent("delete"),
					button -> delete());
		}

		@Override
		protected void setX(int newX) {
			super.setX(newX);
			saveButton.x = newX;
			deleteButton.x = newX + 28;
		}

		@Override
		public void updateNarration(NarrationElementOutput p_169152_) {
		}

		@Override
		public void init() {
		}

		@Override
		protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
				Tesselator tess) {
			this.saveButton.y = this.y + baseY;
			this.deleteButton.y = this.y + baseY;
			saveButton.render(poseStack, mouseX, mouseY, partialTick);
			deleteButton.render(poseStack, mouseX, mouseY, partialTick);
		}

		@Override
		public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
			if (this.saveButton.mouseClicked(p_93641_, p_93642_, p_93643_)
					|| this.deleteButton.mouseClicked(p_93641_, p_93642_, p_93643_))
				return true;
			return super.mouseClicked(p_93641_, p_93642_, p_93643_);
		}
	}

	private void save() {
		GmMod.logger().debug("SAVING THE TEMPLATE !");
		JobTemplate loaded = template.clone();
		JobTemplate.saveJobTemplate(loaded); // saving a clone to avoid sync pblm
		parent.setLoadedTemplate(loaded);
		((GmScreen) parent.getParent()).getJobsLeftPanel().refreshList();
	}

	private void delete() {
		GmMod.logger().debug("DELETING THE TEMPLATE !");
		if (parent.getLoadedTemplate() != null) {
			JobTemplate.deleteJobTemplate(parent.getLoadedTemplate());
			parent.clearInfo();
			((GmScreen) parent.getParent()).getJobsLeftPanel().refreshList();
		}
	}

	private void editName(String newName) {
		if (template != null) {
			template.setName(newName);
			// TODO déplacer dans save
//			((GmScreen) getParent().getParent()).refreshCharactersList();
		}
	}

	@Override
	public void init() {
		for (ScrollableWidget children : getChildrens()) {
			centerX(children);
		}
	}

	public void clearContent() {
		template = null;
		nameWidget.setValue("");
	}

	public void updateContent(JobTemplate template) {
		this.template = template;
		nameWidget.setValue(template.getName());
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
			int i = 0;
			for (ScrollableWidget w : getChildrens()) {
				if (w.visible) {
					w.y = this.y + 11 * i;
					w.render(poseStack, mouseX, mouseY, partialTick);
					i++;
				}
			}
		}
	}

	public void bind(ResourceLocation res) {
		RenderSystem.setShaderTexture(0, res);
	}

	public JobTemplate getTemplate() {
		return template;
	}

	public void setupOverlayRenderState(boolean blend, boolean depthText) {
		setupOverlayRenderState(blend, depthText, Gui.GUI_ICONS_LOCATION);
	}

	public void setupOverlayRenderState(boolean blend, boolean depthTest, @Nullable ResourceLocation texture) {
		if (blend) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
		} else {
			RenderSystem.disableBlend();
		}

		if (depthTest) {
			RenderSystem.enableDepthTest();
		} else {
			RenderSystem.disableDepthTest();
		}

		if (texture != null) {
			RenderSystem.enableTexture();
			bind(texture);
		} else {
			RenderSystem.disableTexture();
		}

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
	}

	@Override
	public int getHeight() {
		int newHeight = 16 + CharacterPanel.PADDING * 2 + CharacterPanel.BORDER * 2;
		for (ScrollableWidget w : getChildrens()) {
			if (w.visible)
				newHeight += w.getHeight();
		}
		this.height = newHeight;
		return newHeight;
	}

	@Override
	protected void updateVisible() {
		this.visible = (template != null);
	}
}
