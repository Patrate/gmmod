package fr.emmuliette.gmmod.gui.gmscreen.panels.jobs;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//@Mod.EventBusSubscriber(modid = GmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@OnlyIn(Dist.CLIENT)
public class JobsLeftPanel extends AbstractWidget {

	public JobsLeftPanel(GmScreen parent, int width, int topPadding) {
		super(0, topPadding, width, parent.height, new TextComponent(""));
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
		if (visible) {
		}
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}
}
