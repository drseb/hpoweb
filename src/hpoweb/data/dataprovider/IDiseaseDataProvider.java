package hpoweb.data.dataprovider;

import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.table.HpoClassTableEntry;

import java.util.Collection;
import java.util.List;

public interface IDiseaseDataProvider extends IEntityDataProvider {

	Collection<String> getAlternativeNames();

	List<HpoClassTableEntry> getAnnotatedHpoClasses();

	Collection<DiseaseGene> getAssociatedGenes();

}
