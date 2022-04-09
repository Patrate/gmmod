package fr.emmuliette.gmmod.characterSheet.stats.attributes;

import java.util.HashSet;
import java.util.Set;

import fr.emmuliette.gmmod.GmMod;
import fr.emmuliette.gmmod.characterSheet.stats.Stat;
import fr.emmuliette.gmmod.exceptions.MissingSheetDataException;
import fr.emmuliette.gmmod.exceptions.StatOutOfBoundsException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public abstract class AttributeStat extends Stat {
	private Attribute attribute;
	private Operation operation;
	private String name;

	public AttributeStat(Attribute attribute, Operation operation) {
		super("");
		setKey("attribute." + this.getClass().getSimpleName());
		this.name = GmMod.MOD_ID + "." + attribute.getRegistryName().getPath() + " _stat";
		this.attribute = attribute;
		this.operation = operation;
		if (attribute instanceof RangedAttribute) {
			RangedAttribute rAttr = (RangedAttribute) attribute;
			this.setMin(rAttr.getMinValue());
			this.setMax(rAttr.getMaxValue());
		}
	}

	@Override
	public void onChange(double oldValue, double newValue) throws StatOutOfBoundsException, MissingSheetDataException {
		super.onChange(oldValue, newValue);
		Entity owner = getOwner();
		if (owner != null && owner instanceof LivingEntity) {
			LivingEntity lEntity = (LivingEntity) owner;
			if (lEntity.getAttribute(attribute) == null)
				throw new MissingSheetDataException(
						"Attribute " + attribute.getRegistryName().getPath() + " is missing for entity ["
								+ lEntity.getClass().getSimpleName() + "] " + lEntity.getName().getContents());
			Set<AttributeModifier> olds = new HashSet<AttributeModifier>();
			for (AttributeModifier am : lEntity.getAttribute(attribute).getModifiers(operation)) {
				if(this instanceof MaxHealth) {
					GmMod.logger().debug("Is " + am.getName() + " equal to " + name + " ? " + (am.getName().equals(name)));
				}
				if (am.getName().equals(name)) {
					olds.add(am);
				}
			}
			lEntity.getAttribute(attribute).addPermanentModifier(new AttributeModifier(name, newValue, operation));
			for (AttributeModifier am : olds) {
				lEntity.getAttribute(attribute).removeModifier(am);
			}
		}
	}
	
	@Override
	public void init() throws StatOutOfBoundsException, MissingSheetDataException {
		onChange(0, this.getValue());
	}
}
