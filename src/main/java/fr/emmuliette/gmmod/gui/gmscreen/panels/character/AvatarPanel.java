package fr.emmuliette.gmmod.gui.gmscreen.panels.character;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.gui.gmscreen.components.ContainerPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AvatarPanel extends ContainerPanel {
	LivingEntity entity;

	public AvatarPanel(CharacterPanel panel, int ratio) {
		super(panel, ratio);
	}

	public void clearContent() {
		entity = null;
	}

	public void updateContent(CharacterSheet sheet) {
		entity = sheet.getOwner();
	}

	@Override
	protected void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick, int entryRight, int baseY,
			Tesselator tess) {
		super.render(poseStack, mouseX, mouseY, partialTick, entryRight, baseY, tess);
		if (this.visible) {
			int i = this.x + this.width / 2;
			int j = this.y + this.height + baseY - 9;
			renderEntityInInventory(i, j, 40, (float) i - mouseX, (float) j - mouseY, entity);
		}
	}

	@Override
	protected void updateVisible() {
		this.visible = (entity != null);
	}

	@Override
	public void init() {
	}

	@SuppressWarnings("resource")
	private static void renderEntityInInventory(int x, int y, int size, float mouseX, float mouseY,
			LivingEntity entity) {
		if (entity instanceof ServerPlayer) {
			entity = Minecraft.getInstance().player;
			// TODO MIEUX !
		}

		float f = (float) Math.atan((double) (mouseX / 40.0F));
		float f1 = (float) Math.atan((double) (mouseY / 40.0F));
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((double) x, (double) y, 1050.0D);
		posestack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		posestack1.translate(0.0D, 0.0D, 1000.0D);
		posestack1.scale((float) size, (float) size, (float) size);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		posestack1.mulPose(quaternion);
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + f * 20.0F;
		entity.setYRot(180.0F + f * 40.0F);
		entity.setXRot(-f1 * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternion1.conj();
		entityrenderdispatcher.overrideCameraOrientation(quaternion1);
		entityrenderdispatcher.setRenderShadow(false);
		if (entityrenderdispatcher.getRenderer(entity) == null) {
			GmMod.logger().warn("Entity renderer is null for " + entity.getName());
			return;
		}
		MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers()
				.bufferSource();
		entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, posestack1, multibuffersource$buffersource,
				15728880);
		multibuffersource$buffersource.endBatch();
		entityrenderdispatcher.setRenderShadow(true);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
	}
}
