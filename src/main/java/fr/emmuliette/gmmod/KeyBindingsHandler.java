package fr.emmuliette.gmmod;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import fr.emmuliette.gmmod.gui.gmscreen.GmScreen;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class KeyBindingsHandler {
	public static final KeyMapping GM_SCREEN = new KeyMapping("key.gmscreen", KeyConflictContext.UNIVERSAL,
			InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.gmscreen");
	private static final GmScreen INSTANCE = new GmScreen();

	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event) {
		ClientRegistry.registerKeyBinding(GM_SCREEN);
	}

	@SubscribeEvent
	public static void onKeyPress(InputEvent.KeyInputEvent e) {
		// Press key
		if (e.getAction() == GLFW.GLFW_PRESS) {
			if (e.getKey() == GM_SCREEN.getKey().getValue()) {
				if (INSTANCE.isOpen())
					INSTANCE.close();
				else
					INSTANCE.open();
				return;
			}
			return;
		}

		// Release key
		if (e.getAction() == GLFW.GLFW_RELEASE) {
			return;
		}
	}
}
