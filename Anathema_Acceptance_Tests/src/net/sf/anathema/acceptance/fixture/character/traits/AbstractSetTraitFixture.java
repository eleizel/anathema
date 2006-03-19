package net.sf.anathema.acceptance.fixture.character.traits;

import net.sf.anathema.acceptance.fixture.character.CharacterSummary;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.library.trait.ITrait;
import net.sf.anathema.character.model.ICharacter;
import net.sf.anathema.character.model.ICharacterStatistics;
import fitnesse.fixtures.RowEntryFixture;

public abstract class AbstractSetTraitFixture extends RowEntryFixture {

  public String traitType;
  public int value;

  @Override
  public void enterRow() throws Exception {
    ITrait ability = getTrait();
    ability.setCurrentValue(value);
  }

  protected final ITrait getTrait() {
    ICharacter character = new CharacterSummary(summary).getCharacter();
    ICharacterStatistics statistics = character.getStatistics();
    return statistics.getTraitConfiguration().getTrait(getTraitType());
  }

  protected abstract ITraitType getTraitType();
}