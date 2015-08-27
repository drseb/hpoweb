package hpoweb.data.dataprovider;

import hpoweb.uicontent.table.HpoClassTableEntry;

import java.util.Collection;
import java.util.List;

import de.charite.phenowl.annotations.DiseaseEntry;

public interface IGeneDataProvider extends IEntityDataProvider {

	List<HpoClassTableEntry> getAssociatedHpoClasses();

	Collection<DiseaseEntry> getAssociatedDiseases();

}
