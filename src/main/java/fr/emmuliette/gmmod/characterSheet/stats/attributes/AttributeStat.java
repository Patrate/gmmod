package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import java.util.HashSet;
import java.util.Set;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public abstract class AttributeStat extends Stat {
	private Attribute attribute;
	private Operation operation;
	private int ratio;
	private String name;

	public AttributeStat(Attribute attribute, Operation operation) {
		this(attribute, operation, 1);
	}

	public AttributeStat(Attribute attribute, Operation operation, int ratio) {
		super("");
		setKey("attribute." + this.getClass().getSimpleName());
		if (ratio < 1)
			// TODO throw error
			GmMod.logger().warn("Ratio is " + ratio + " for attributeStat " + getKey());
		this.name = GmMod.MOD_ID + "." + attribute.getRegistryName().getPath() + " _stat";
		this.attribute = attribute;
		this.operation = operation;
		this.ratio = ratio;
		if (attribute instanceof RangedAttribute) {
			RangedAttribute rAttr = (RangedAttribute) attribute;
			this.setMin((int) ((rAttr.getMinValue() - rAttr.getDefaultValue()) * ratio));
			this.setMax((int) ((rAttr.getMaxValue() - rAttr.getDefaultValue()) * ratio));
		}
	}

	@Override
	public void onChange(int oldValue, int newValue) throws StatOutOfBoundsException, MissingSheetDataException {
		super.onChange(oldValue, newValue);
		LivingEntity owner = getOwner();
		if (owner != null) {
			if (owner.getAttribute(attribute) == null)
				throw new MissingSheetDataException(
						"Attribute " + attribute.getRegistryName().getPath() + " is missing for entity ["
								+ owner.getClass().getSimpleName() + "] " + owner.getName().getContents());
			Set<AttributeModifier> olds = new HashSet<AttributeModifier>();
			for (AttributeModifier am : owner.getAttribute(attribute).getModifiers(operation)) {
				if (am.getName().equals(name)) {
					olds.add(am);
				}
			}
			double modifier = ((double) newValue) * 1d / ratio;
			GmMod.logger().debug("Modifier is " + modifier);
			owner.getAttribute(attribute).addPermanentModifier(new AttributeModifier(name, modifier, operation));
			for (AttributeModifier am : olds) {
				owner.getAttribute(attribute).removeModifier(am);
			}
		}
	}

	@Override
	public void init() throws StatOutOfBoundsException, MissingSheetDataException {
		onChange(0, this.getValue());
	}

	@Override
	public boolean isValid() {
		LivingEntity owner = getOwner();
		if (owner == null)
			return false;
		if (owner.getAttribute(attribute) == null)
			return false;
		return true;
	}
}
