package fr.emmuliette.gmmod.gui.gmscreen;

import java.util.HashSet;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.components.CharacterSelectorListener;
import fr.emmuliette.gmmod.gui.gmscreen.components.TabSelectorListener;
import fr.emmuliette.gmmod.gui.gmscreen.components.TabsWidget;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterListWidget;
import fr.emmuliette.gmmod.gui.gmscreen.panels.character.CharacterPanel;
import fr.emmuliette.gmmod.gui.gmscreen.panels.jobs.JobsLeftPanel;
import fr.emmuliette.gmmod.gui.gmscreen.panels.jobs.JobsPanel;
import fr.emmuliette.gmmod.gui.gmscreen.panels.rules.RulesPanel;
import fr.emmuliette.gmmod.gui.gmscreen.panels.skills.SkillsPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GmScreen extends Screen implements CharacterSelectorListener, TabSelectorListener {
	private static final int LEFT_PANEL_PADDING = 20, LEFT_PANEL_WIDTH = 80;
	private static final ResourceLocation GM_SCREEN_RESOURCE = new ResourceLocation(GmMod.MOD_ID,
			"textures/gui/gmscreen.png");
	private static final int TAB_CHARACTER = 0, TAB_JOBS = 1, TAB_SKILLS = 2, TAB_RULES = 3;

	private TabsWidget tabs;

	private CharacterListWidget charactersList;
	private JobsLeftPanel jobsLeftPanel;

	private CharacterPanel characterPanel;
	private JobsPanel jobsPanel;
	private SkillsPanel skillsPanel;
	private RulesPanel rulesPanel;

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
		this.minecraft.setScreen(this);
		selectTab(0);
	}

	public void close() {
		this.minecraft.setScreen(null);
	}

	@Override
	protected void init() {
		tickables = new HashSet<Tickable>();
		this.charactersList = new CharacterListWidget(this, LEFT_PANEL_WIDTH, LEFT_PANEL_PADDING, 18);
		this.addRenderableWidget(charactersList);

		this.jobsLeftPanel = new JobsLeftPanel(this, LEFT_PANEL_WIDTH, LEFT_PANEL_PADDING);
		this.addRenderableWidget(jobsLeftPanel);

		this.tabs = new TabsWidget(this, 0, 0, LEFT_PANEL_WIDTH, LEFT_PANEL_PADDING);
		tabs.addTab(GM_SCREEN_RESOURCE, 30, 1, 14, 10, "gui.gmscreen.character");
		tabs.addTab(GM_SCREEN_RESOURCE, 45, 1, 14, 10, "gui.gmscreen.jobs");
		tabs.addTab(GM_SCREEN_RESOURCE, 60, 1, 14, 10, "gui.gmscreen.skills");
		tabs.addTab(GM_SCREEN_RESOURCE, 75, 1, 14, 10, "gui.gmscreen.rules");
		this.addRenderableWidget(tabs);

		this.characterPanel = new CharacterPanel(minecraft, this, (width - charactersList.getRight()), height, 0,
				charactersList.getRight());
		this.addRenderableWidget(characterPanel);

		this.jobsPanel = new JobsPanel(minecraft, this, (width - charactersList.getRight()), height, 0,
				charactersList.getRight());
		this.addRenderableWidget(characterPanel);

		this.skillsPanel = new SkillsPanel(minecraft, this, (width - charactersList.getRight()), height, 0,
				charactersList.getRight());
		this.addRenderableWidget(characterPanel);

		this.rulesPanel = new RulesPanel(minecraft, this, (width - charactersList.getRight()), height, 0,
				charactersList.getRight());
		this.addRenderableWidget(characterPanel);
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		renderBackground(stack);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.vertex((double) 0, (double) this.height, 0.0D)
				.uv((float) 0 / 32.0F, (float) (this.height) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex((double) LEFT_PANEL_WIDTH, (double) this.height, 0.0D)
				.uv((float) LEFT_PANEL_WIDTH / 32.0F, (float) (this.height) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex((double) LEFT_PANEL_WIDTH, (double) 0D, 0.0D)
				.uv((float) LEFT_PANEL_WIDTH / 32.0F, (float) (0D) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex((double) 0, (double) 0d, 0.0D).uv((float) 0 / 32.0F, (float) (0) / 32.0F)
				.color(32, 32, 32, 255).endVertex();
		tesselator.end();

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferbuilder.vertex((double) 0, (double) LEFT_PANEL_PADDING, 0.0D)
				.uv((float) 0 / 32.0F, (float) (LEFT_PANEL_PADDING) / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex((double) LEFT_PANEL_WIDTH, (double) LEFT_PANEL_PADDING, 0.0D)
				.uv((float) LEFT_PANEL_WIDTH / 32.0F, (float) (LEFT_PANEL_PADDING) / 32.0F).color(64, 64, 64, 255)
				.endVertex();
		bufferbuilder.vertex((double) LEFT_PANEL_WIDTH, (double) 0D, 0.0D)
				.uv((float) LEFT_PANEL_WIDTH / 32.0F, (float) (0D) / 32.0F).color(64, 64, 64, 255).endVertex();
		bufferbuilder.vertex((double) 0, (double) 0d, 0.0D).uv((float) 0 / 32.0F, (float) (0) / 32.0F)
				.color(64, 64, 64, 255).endVertex();
		tesselator.end();

		super.render(stack, mouseX, mouseY, partialTick);
	}

	@Override
	public void selectCharacter(LivingEntity entity) {
		if (entity == null)
			// TODO throw error
			return;
		CharacterSheet sheet = entity.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
		if (sheet == null)
			return;

		characterPanel.setInfo(sheet);
	}

	public Font getFontRenderer() {
		return font;
	}

	@Override
	public void selectTab(int id) {
		switch (id) {
		case TAB_CHARACTER:
			characterPanel.setVisible(true);
			jobsPanel.setVisible(false);
			skillsPanel.setVisible(false);
			rulesPanel.setVisible(false);

			charactersList.setVisible(true);
			jobsLeftPanel.visible = false;
			break;

		case TAB_JOBS:
			characterPanel.setVisible(false);
			jobsPanel.setVisible(true);
			skillsPanel.setVisible(false);
			rulesPanel.setVisible(false);

			charactersList.setVisible(false);
			jobsLeftPanel.visible = true;
			break;

		case TAB_SKILLS:
			characterPanel.setVisible(false);
			jobsPanel.setVisible(false);
			skillsPanel.setVisible(true);
			rulesPanel.setVisible(false);

			charactersList.setVisible(false);
			jobsLeftPanel.visible = false;
			break;

		case TAB_RULES:
			characterPanel.setVisible(false);
			jobsPanel.setVisible(false);
			skillsPanel.setVisible(false);
			rulesPanel.setVisible(true);

			charactersList.setVisible(false);
			jobsLeftPanel.visible = false;
			break;

		default:
			characterPanel.setVisible(false);
			jobsPanel.setVisible(false);
			skillsPanel.setVisible(false);
			rulesPanel.setVisible(false);

			charactersList.setVisible(false);
			jobsLeftPanel.visible = false;
		}
	}

	private Set<Tickable> tickables;

	public void registerTickable(Tickable t) {
		tickables.add(t);
	}

	@Override
	public void tick() {
		super.tick();
		for (Tickable t : tickables) {
			t.tick();
		}
	}

	public void refreshCharactersList() {
		charactersList.refreshList();
	}
}
