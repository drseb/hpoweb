package hpoweb.data;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;

import com.vaadin.server.VaadinService;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.annotations.DiseaseGeneMapper;
import de.charite.phenowl.hpowl.util.ExtendedOwlOntology;
import de.charite.phenowl.hpowl.util.OwlAxiomClass;

public class HpData {

	private ExtendedOwlOntology extOwlOntology;

	private AnnotationUtils annotationUtils;

	public HpData() {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

		final String xmlFile = basepath + "/WEB-INF/data/hp/catalog-v001.xml";
		String inputOntologyFile = basepath + "/WEB-INF/data/hp/hp-edit.owl";

		Set<OwlAxiomClass> remove = new HashSet<>();
		remove.add(OwlAxiomClass.EQUIVALENCE);
		extOwlOntology = new ExtendedOwlOntology(inputOntologyFile, xmlFile, remove);

		// debug
		OWLClass cls = extOwlOntology.getOWLClassForIRI(IRI.create("http://purl.obolibrary.org/obo/HP_0100533"));
		System.out.println("ancestors of " + cls);
		for (OWLClass c : extOwlOntology.getAncestorsAsserted(cls)) {
			System.out.println(" - " + extOwlOntology.getLabel(c.getIRI()));
		}

		annotationUtils = new AnnotationUtils(extOwlOntology);
		String datapath = basepath + "/WEB-INF/data/biodata/";
		DiseaseGeneMapper.setOrphanetPath(datapath);
		annotationUtils.setDataPath(datapath);
		annotationUtils.parseAssociation();

	}

	public ExtendedOwlOntology getExtOwlOntology() {
		return extOwlOntology;
	}

	public AnnotationUtils getAnnotationUtils() {
		return annotationUtils;
	}
}
