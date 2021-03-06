package net.sf.anathema.character.sidereal.colleges.model;

import net.sf.anathema.character.generic.template.points.IFavorableTraitCreationPoints;
import net.sf.anathema.character.library.trait.AbstractFavorableTraitCostCalculator;
import net.sf.anathema.character.library.trait.IAdditionalTraitBonusPointManagement;
import net.sf.anathema.character.library.trait.IFavorableDefaultTrait;

public class CollegeBonusPointCalculator extends AbstractFavorableTraitCostCalculator {

  private final ICollegeBonusPointCosts costs;

  public CollegeBonusPointCalculator(
      IAdditionalTraitBonusPointManagement additionalPools,
      IFavorableTraitCreationPoints points,
      IFavorableDefaultTrait[] traits,
      ICollegeBonusPointCosts costs) {
    super(additionalPools, points, 3, traits);
    this.costs = costs;
  }

  @Override
  protected int getCostFactor(IFavorableDefaultTrait college) {
    return costs.getCollegeCosts(college.getFavorization().isCasteOrFavored());
  }
}