package net.sf.anathema.character.spirit;

import net.sf.anathema.character.generic.framework.ICharacterGenerics;
import net.sf.anathema.character.generic.framework.magic.FirstExcellency;
import net.sf.anathema.character.generic.framework.magic.SecondExcellency;
import net.sf.anathema.character.generic.framework.magic.ThirdExcellency;
import net.sf.anathema.character.generic.framework.module.NullObjectCharacterModuleAdapter;
import static net.sf.anathema.character.generic.impl.rules.ExaltedEdition.SecondEdition;
import net.sf.anathema.character.generic.impl.rules.ExaltedSourceBook;
import net.sf.anathema.character.generic.impl.traits.EssenceTemplate;
import net.sf.anathema.character.generic.magic.IMagicStats;
import net.sf.anathema.character.reporting.CharacterReportingModule;
import net.sf.anathema.character.reporting.CharacterReportingModuleObject;
import net.sf.anathema.character.reporting.pdf.layout.extended.ExtendedEncodingRegistry;
import net.sf.anathema.character.reporting.pdf.layout.simple.ISimplePartEncoder;
import net.sf.anathema.character.reporting.pdf.layout.simple.SimpleEncodingRegistry;
import net.sf.anathema.character.spirit.generic.DivineSubordination;
import net.sf.anathema.character.spirit.generic.InfiniteMastery;
import net.sf.anathema.character.spirit.reporting.Extended2ndEditionSpiritPartEncoder;
import net.sf.anathema.character.spirit.reporting.Simple2ndEditionSpiritPartEncoder;
import net.sf.anathema.lib.resources.IResources;

import static net.sf.anathema.character.generic.type.CharacterType.SPIRIT;

public class SpiritCharacterModule extends NullObjectCharacterModuleAdapter {

  private static final int ESSENCE_MAX = EssenceTemplate.SYSTEM_ESSENCE_MAX;

  @Override
  public void registerCommonData(ICharacterGenerics characterGenerics) {
    FirstExcellency firstExcellency = new FirstExcellency(SPIRIT, ExaltedSourceBook.SecondEdition, "1 m per die");
    SecondExcellency secondExcellency = new SecondExcellency(SPIRIT, ExaltedSourceBook.SecondEdition);
    ThirdExcellency thirdExcellency = new ThirdExcellency(SPIRIT, "4 m", ExaltedSourceBook.SecondEdition);
    characterGenerics.getGenericCharmStatsRegistry().register(SPIRIT, new IMagicStats[]{firstExcellency, secondExcellency, thirdExcellency,
                                                                                        new InfiniteMastery(), new DivineSubordination()});
  }

  @Override
  public void addCharacterTemplates(ICharacterGenerics characterGenerics) {
    registerParsedTemplate(characterGenerics, "template/Spirit2nd.template"); //$NON-NLS-1$
  }

  @Override
  public void addReportTemplates(ICharacterGenerics generics, IResources resources) {
    CharacterReportingModuleObject moduleObject = generics.getModuleObjectMap().getModuleObject(CharacterReportingModule.class);
    registerSimpleEncoders(resources, moduleObject);
    registerExtendedEncoders(resources, moduleObject);
  }

  private void registerSimpleEncoders(IResources resources, CharacterReportingModuleObject moduleObject) {
    SimpleEncodingRegistry registry = moduleObject.getSimpleEncodingRegistry();
    registry.setPartEncoder(SPIRIT, SecondEdition, new Simple2ndEditionSpiritPartEncoder(resources, registry, ESSENCE_MAX));
  }

  private void registerExtendedEncoders(IResources resources, CharacterReportingModuleObject moduleObject) {
    ExtendedEncodingRegistry registry = moduleObject.getExtendedEncodingRegistry();
    registry.setPartEncoder(SPIRIT, SecondEdition, new Extended2ndEditionSpiritPartEncoder(resources, registry, ESSENCE_MAX));
  }
}
