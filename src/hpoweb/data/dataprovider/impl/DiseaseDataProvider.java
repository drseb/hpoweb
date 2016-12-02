package hpoweb.data.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.HashMultimap;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.annotations.HPOAnnotation;
import de.charite.phenowl.annotations.OwlAnnotatedDiseaseEntry;
import de.charite.phenowl.hpowl.util.OboUtil;
import hpo.DiseaseId;
import hpoweb.data.HpData;
import hpoweb.data.dataprovider.IDiseaseDataProvider;
import hpoweb.data.entities.DiseaseGene;
import hpoweb.uicontent.table.HpoClassTableEntry;

public class DiseaseDataProvider implements IDiseaseDataProvider {

	private HpData hpData;
	private AnnotationUtils annotationUtils;
	private DiseaseId diseaseId;
	private OwlAnnotatedDiseaseEntry diseaseEntry;
	private ArrayList<HPOAnnotation> annotations;
	private HashSet<Integer> diseaseGeneIds;
	private HashSet<DiseaseGene> diseaseGenes;

	public DiseaseDataProvider(DiseaseId diseaseId, HpData hpData) {
		this.hpData = hpData;
		this.diseaseId = diseaseId;
		this.annotationUtils = hpData.getAnnotationUtils();
		this.diseaseEntry = annotationUtils.getDiseaseId2entry().get(diseaseId);
		this.annotations = diseaseEntry.getAnnotationsOwlClasses();
		// genes for this disease
		this.diseaseGeneIds = annotationUtils.getDiseaseGeneMapper().diseaseId2entrezIds.get(diseaseId);
		diseaseGenes = new HashSet<DiseaseGene>();
		if (diseaseGeneIds != null)
			for (int entrez : diseaseGeneIds) {
				String symbol = annotationUtils.getDiseaseGeneMapper().entrez2symbol.get(entrez);
				DiseaseGene g = new DiseaseGene(entrez + "", symbol);
				diseaseGenes.add(g);
			}
	}

	@Override
	public String getId() {
		return diseaseId.toString();
	}

	@Override
	public String getLabel() {
		return diseaseEntry.getName();
	}

	@Override
	public String getTypeOfEntityString() {
		return "disease entry";
	}

	@Override
	public Collection<String> getAlternativeNames() {
		return diseaseEntry.getAlternativeNames();
	}

	@Override
	public List<HpoClassTableEntry> getAnnotatedHpoClasses() {
		ArrayList<HpoClassTableEntry> tableentries = new ArrayList<HpoClassTableEntry>();

		HashMultimap<OWLClass, HPOAnnotation> owlclass2annotations = HashMultimap.create();

		for (HPOAnnotation annotation : this.annotations) {
			OWLClass cl = annotation.getAnnotatedOWLClass();
			owlclass2annotations.put(cl, annotation);
		}

		for (OWLClass owlClass : owlclass2annotations.keySet()) {
			String label = hpData.getExtOwlOntology().getLabel(owlClass.getIRI());
			String id = OboUtil.IRI2ID(owlClass.getIRI());
			String description = generateAnnotationDescription(owlclass2annotations.get(owlClass));
			HpoClassTableEntry tableentry = new HpoClassTableEntry(id, label, description);
			tableentries.add(tableentry);
		}
		return tableentries;
	}

	private String generateAnnotationDescription(Set<HPOAnnotation> annotations) {

		StringBuffer buffer = new StringBuffer("");

		boolean multipleAnnotations = annotations.size() > 1;

		if (multipleAnnotations) {
			buffer.append("There are " + annotations.size() + " annotations supporting this association.<br>");
		}

		int i = 0;
		for (HPOAnnotation annotation : annotations) {
			i++;
			String freq = annotation.getFrequencyModifier();
			String who = annotation.getAssignedBy();
			String date = annotation.getDate();
			OWLClass onset = annotation.getOnsetModifier();

			if (multipleAnnotations) {
				buffer.append(" Meta data for annotation " + i + ":<br>");
			}
			else {
				buffer.append(" Meta data for this annotation:<br>");
			}

			if (freq != null && (!freq.equals(""))) {
				buffer.append("&nbsp;&nbsp;&nbsp;- Frequency: " + freq + "<br>");
			}
			if (who != null && (!who.equals(""))) {
				buffer.append("&nbsp;&nbsp;&nbsp;- Curator: " + who + " (" + date + ")<br>");
			}
			if (onset != null && (!onset.equals(""))) {
				String onsetLabel = hpData.getExtOwlOntology().getLabel(onset.getIRI());
				buffer.append("&nbsp;&nbsp;&nbsp;- Onset of this symptom: " + onsetLabel + "<br>");
			}
		}

		return buffer.toString();

	}

	@Override
	public Collection<DiseaseGene> getAssociatedGenes() {
		return diseaseGenes;
	}
}
