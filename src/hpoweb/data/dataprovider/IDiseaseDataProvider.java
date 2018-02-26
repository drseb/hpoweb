package hpoweb.data.dataprovider;

import java.util.Collection;
import java.util.List;

import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.table.HpoClassTableEntry;

public interface IDiseaseDataProvider extends IEntityDataProvider {

	Collection<String> getAlternativeNames();

	List<HpoClassTableEntry> getAnnotatedHpoClasses();

	Collection<DiseaseGene> getAssociatedGenes();

}
