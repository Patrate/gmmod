package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import fr.emmuliette.gmmod.gui.gmscreen.components.CharacterSelectorListener;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@OnlyIn(Dist.CLIENT)
public class CharacterListWidget extends ObjectSelectionList<CharacterListWidget.CharacterEntry> {
	private static final Set<LivingEntity> namedEntities = new HashSet<LivingEntity>();
	private final int listWidth;
	private Set<LivingEntity> characters;
	private Font font;
	private boolean visible;

	private CharacterSelectorListener parent;

	public CharacterListWidget(GmScreen parent, int width, int topPadding, int bottomPadding) {
		super(parent.getMinecraft(), width, parent.height, topPadding, parent.height - bottomPadding,
				parent.getFontRenderer().lineHeight * 2 + 8);
		this.parent = parent;
		this.visible = true;
		this.font = parent.getFontRenderer();
		this.listWidth = width;
		this.characters = new HashSet<LivingEntity>();
		for (Player p : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			characters.add(p);
		}
		for (LivingEntity e : namedEntities) {
			if (e.hasCustomName())
				characters.add(e);
		}
		this.setRenderBackground(false);
		this.refreshList();
	}

	@SubscribeEvent
	public static void onUpdate(LivingUpdateEvent event) {
		if (event.getEntity() instanceof Player)
			return;
		if (event.getEntity().hasCustomName())
			namedEntities.add(event.getEntityLiving());
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
		List<LivingEntity> toRemove = new ArrayList<LivingEntity>();
		for (LivingEntity e : characters) {
			if ((e instanceof Player) || (e.isAlive() && e.hasCustomName())) {
				this.addEntry(new CharacterEntry(e));
			} else {
				toRemove.add(e);
			}
		}
		for (LivingEntity e : toRemove)
			characters.remove(e);

	}

	public class CharacterEntry extends ObjectSelectionList.Entry<CharacterEntry> {
		private final String entityName;
		private final LivingEntity character;

		CharacterEntry(LivingEntity character) {
			this.character = character;
			this.entityName = character.getName().getContents();
		}

		@Override
		public Component getNarration() {
			return new TranslatableComponent("narrator.select", entityName);
		}

		@Override
		public void render(PoseStack poseStack, int entryIdx, int top, int left, int entryWidth, int entryHeight,
				int mouseX, int mouseY, boolean p_194999_5_, float partialTick) {
			font.draw(poseStack, new TextComponent(entityName), left + 3, top + 2, 0xFFFFFF);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.equals(getSelected()))
				return false;
			setSelected(this);
			return true;
		}

		public String getEntityName() {
			return entityName;
		}

		public LivingEntity getCharacter() {
			return character;
		}
	}

	@Override
	public void setSelected(CharacterEntry p) {

		super.setSelected(p);
		parent.selectCharacter(p.getCharacter());
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
