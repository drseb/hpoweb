package hpoweb.data;

import com.vaadin.server.VaadinService;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.hpowl.util.ExtendedOwlOntology;

public class HpData {

	private ExtendedOwlOntology extOwlOntology;
	private AnnotationUtils annotationUtils;

	public HpData() {
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

		final String xmlFile = basepath + "/WEB-INF/data/hp/catalog-v001.xml";
		String inputOntologyFile = basepath + "/WEB-INF/data/hp/hp-edit.owl";

		extOwlOntology = new ExtendedOwlOntology(inputOntologyFile, xmlFile);

		annotationUtils = new AnnotationUtils(extOwlOntology);
		annotationUtils.setDataPath(basepath + "/WEB-INF/data/biodata/");
		annotationUtils.parseAssociation();

	}

	public ExtendedOwlOntology getExtOwlOntology() {
		return extOwlOntology;
	}

	public AnnotationUtils getAnnotationUtils() {
		return annotationUtils;
	}
}
