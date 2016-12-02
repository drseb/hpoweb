package hpoweb.data.dataprovider;

import java.util.Collection;
import java.util.List;

import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import hpoweb.uicontent.table.HpoClassTableEntry;

public interface IGeneDataProvider extends IEntityDataProvider {

	List<HpoClassTableEntry> getAssociatedHpoClasses();

	Collection<OwlAnnotatedDiseaseEntry> getAssociatedDiseases();

}
