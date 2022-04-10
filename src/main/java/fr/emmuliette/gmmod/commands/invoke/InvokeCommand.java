package fr.emmuliette.gmmod.commands.invoke;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.CharacterSheet;
import fr.emmuliette.gmmod.characterSheet.stats.attributes.MaxHealth;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/* Registered in ModEventListener */
public class InvokeCommand {
//	TODO Invoke command !
	
	@SuppressWarnings("deprecation")
	public static final SuggestionProvider<CommandSourceStack> INVOKABLE_ENTITIES = SuggestionProviders
			.register(new ResourceLocation("invokable_entities"), (p_212438_, p_212439_) -> {
				return SharedSuggestionProvider.suggestResource(
						Registry.ENTITY_TYPE.stream().filter(EntityType::canSummon), p_212439_, EntityType::getKey,
						(p_212436_) -> {
							return new TranslatableComponent(
									Util.makeDescriptionId("entity", EntityType.getKey(p_212436_)));
						});
			});

	private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(
			new TranslatableComponent("commands.summon.failed"));
	private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(
			new TranslatableComponent("commands.summon.failed.uuid"));
	private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(
			new TranslatableComponent("commands.summon.invalidPosition"));

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("invoke").requires((p_138819_) -> {
			return p_138819_.hasPermission(2);
		}).then(Commands.argument("entity", EntitySummonArgument.id()).suggests(INVOKABLE_ENTITIES)
				.executes((p_138832_) -> {
					return spawnEntity(p_138832_.getSource(),
							EntitySummonArgument.getSummonableEntity(p_138832_, "entity"),
							p_138832_.getSource().getPosition(), new CompoundTag(), true);
				}).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_138830_) -> {
					return spawnEntity(p_138830_.getSource(),
							EntitySummonArgument.getSummonableEntity(p_138830_, "entity"),
							Vec3Argument.getVec3(p_138830_, "pos"), new CompoundTag(), true);
				}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((p_138817_) -> {
					return spawnEntity(p_138817_.getSource(),
							EntitySummonArgument.getSummonableEntity(p_138817_, "entity"),
							Vec3Argument.getVec3(p_138817_, "pos"),
							CompoundTagArgument.getCompoundTag(p_138817_, "nbt"), false);
				})))));
	}

	private static void invokeEntity(Mob mob) {
		CharacterSheet mobSheet = mob.getCapability(CharacterSheet.SHEET_CAPABILITY).orElse(null);
		GmMod.logger().debug("Character sheet for entity is " + ((mobSheet == null) ? "absent" : "present"));
		if (mobSheet != null) {
			GmMod.logger().debug("Mob life before is " + mob.getHealth());
			mobSheet.setStat(MaxHealth.class, 20);
			mob.setHealth(mob.getMaxHealth());
			GmMod.logger().debug("Mob life is now " + mob.getHealth());
		}
	}

	private static int spawnEntity(CommandSourceStack p_138821_, ResourceLocation p_138822_, Vec3 p_138823_,
			CompoundTag p_138824_, boolean p_138825_) throws CommandSyntaxException {
		BlockPos blockpos = new BlockPos(p_138823_);
		if (!Level.isInSpawnableBounds(blockpos)) {
			throw INVALID_POSITION.create();
		} else {
			CompoundTag compoundtag = p_138824_.copy();
			compoundtag.putString("id", p_138822_.toString());
			ServerLevel serverlevel = p_138821_.getLevel();
			Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (p_138828_) -> {
				p_138828_.moveTo(p_138823_.x, p_138823_.y, p_138823_.z, p_138828_.getYRot(), p_138828_.getXRot());
				return p_138828_;
			});
			if (entity == null) {
				throw ERROR_FAILED.create();
			} else {
				if (entity instanceof Mob) {
					invokeEntity((Mob) entity);
					if (p_138825_) {
						((Mob) entity).finalizeSpawn(p_138821_.getLevel(),
								p_138821_.getLevel().getCurrentDifficultyAt(entity.blockPosition()),
								MobSpawnType.COMMAND, (SpawnGroupData) null, (CompoundTag) null);

					}
				}

				if (!serverlevel.tryAddFreshEntityWithPassengers(entity)) {
					throw ERROR_DUPLICATE_UUID.create();
				} else {
					p_138821_.sendSuccess(new TranslatableComponent("commands.summon.success", entity.getDisplayName()),
							true);
					return 1;
				}
			}
		}
	}

}
