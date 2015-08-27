package hpoweb.data.dataprovider;

import hpoweb.uicontent.graph.GraphtestUI;
import hpoweb.uicontent.table.DiseaseGeneTableEntry;
import hpoweb.uicontent.table.GeneDiseaseTableEntry;

import java.util.Collection;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

public interface IHpClassDataProvider extends IEntityDataProvider {

	public String getPrimaryId();

	public Collection<String> getAlternativeIds();

	public GraphtestUI getGraphtestUi();

	public String getIRI();

	public Collection<String> getSynonyms();

	public String getTextdef();

	public String getLogicalDef();

	public Collection<OWLClass> getSuperClasses();

	public Collection<OWLClass> getSubClasses();

	public List<DiseaseGeneTableEntry> getDiseaseGeneTableContent();

	public List<GeneDiseaseTableEntry> getGeneDiseaseTableContent();

}
