package hpoweb.data.dataprovider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.semanticweb.owlapi.model.OWLClass;

import de.charite.phenowl.annotations.AnnotationUtils;
import de.charite.phenowl.hpowl.util.OboUtil;
import hpo.DiseaseGeneMapper;
import hpo.Item;
import hpo.ItemId;
import hpoweb.data.HpData;
import hpoweb.data.entities.SearchableEntity;
import hpoweb.data.entities.SearchableEntity.SearchableEntityType;

public class LazySearchbarService {

	private List<SearchableEntity> all = null;

	private String currentFilter = null;
	private List<SearchableEntity> currentResultList = null;

	public LazySearchbarService(HpData hpdata) {

		all = new ArrayList<SearchableEntity>();
		HashSet<String> alreadyAddedSearchFields = new HashSet<String>();

		if (hpdata != null) {

			/*
			 * Hpo classes
			 */
			for (OWLClass cls : hpdata.getExtOwlOntology().getAllClasses()) {
				// filter hp-classes -> iri must contain HP_
				if (!cls.getIRI().toString().contains("HP_"))
					continue;
				String label = hpdata.getExtOwlOntology().getLabel(cls.getIRI());
				String hpId = OboUtil.IRI2ID(cls.getIRI());
				String searchAble = hpId + " " + label;
				SearchableEntity entity = new SearchableEntity(searchAble, hpId, SearchableEntityType.HPO);

				addIfNew(all, entity, alreadyAddedSearchFields);

				for (String syn : hpdata.getExtOwlOntology().getSynonymsForClass(cls)) {
					String searchAbleSyn = hpId + " " + syn;
					SearchableEntity entitySyn = new SearchableEntity(searchAbleSyn, hpId, SearchableEntityType.HPO);
					// all.add(entitySyn);
					addIfNew(all, entitySyn, alreadyAddedSearchFields);
				}

				for (String altId : hpdata.getExtOwlOntology().getAlternativeIdsForClass(cls)) {
					String searchAbleAlt = hpId + " " + altId;
					SearchableEntity entityAlt = new SearchableEntity(searchAbleAlt, hpId, SearchableEntityType.HPO);
					// all.add(entityAlt);
					addIfNew(all, entityAlt, alreadyAddedSearchFields);

				}

			}

			for (OWLClass cls : hpdata.getExtOwlOntology().getAllClasses()) {
				// filter hp-classes -> iri must contain HP_
				if (!cls.getIRI().toString().contains("HP_"))
					continue;
				String hpId = OboUtil.IRI2ID(cls.getIRI());

				String textDef = hpdata.getExtOwlOntology().getTextdefForClass(cls);
				if (textDef != null) {
					SearchableEntity entityTextdef = new SearchableEntity(textDef, hpId, SearchableEntityType.HPO);
					// all.add(entityTextdef);
					addIfNew(all, entityTextdef, alreadyAddedSearchFields);

				}

				String logicalDef = hpdata.getExtOwlOntology().getLogicalDefForClass(cls);
				if (logicalDef != null) {
					SearchableEntity entityLogicaldef = new SearchableEntity(logicalDef, hpId, SearchableEntityType.HPO);
					// all.add(entityLogicaldef);
					addIfNew(all, entityLogicaldef, alreadyAddedSearchFields);

				}
			}

			/*
			 * Diseases
			 */
			AnnotationUtils annotationUtils = hpdata.getAnnotationUtils();
			for (ItemId id : annotationUtils.getDiseaseId2entry().keySet()) {
				Item diseaseEntry = annotationUtils.getDiseaseId2entry().get(id);
				String diseaseId = diseaseEntry.getDiseaseIdAsString();
				String searchAble = diseaseId + " " + diseaseEntry.getName();

				SearchableEntity entity = new SearchableEntity(searchAble, diseaseId, SearchableEntityType.Disease);
				// all.add(entity);
				addIfNew(all, entity, alreadyAddedSearchFields);

				for (String altName : diseaseEntry.getAlternativeNames()) {
					String searchAbleAlt = diseaseId + " " + altName;
					SearchableEntity entityAlt = new SearchableEntity(searchAbleAlt, diseaseId, SearchableEntityType.Disease);
					// all.add(entityAlt);
					addIfNew(all, entityAlt, alreadyAddedSearchFields);

				}
			}
			/*
			 * Genes
			 */
			DiseaseGeneMapper diseaseGeneMapper = annotationUtils.getDiseaseGeneMapper();
			for (int entrezId : diseaseGeneMapper.entrez2symbol.keySet()) {
				if (diseaseGeneMapper.entrezId2diseaseIds.containsKey(entrezId) && diseaseGeneMapper.entrezId2diseaseIds.get(entrezId).size() > 0) {
					String geneId = entrezId + "";
					String searchAble = diseaseGeneMapper.entrez2symbol.get(entrezId) + " / " + geneId;
					SearchableEntity entity = new SearchableEntity(searchAble, geneId, SearchableEntityType.Gene);
					// all.add(entity);
					addIfNew(all, entity, alreadyAddedSearchFields);

				}
			}

		}
		else {
			Random r = new Random(System.currentTimeMillis());
			for (int i = 0; i < 100000; i++) {
				String randId = "HP:" + RandomStringUtils.randomNumeric(7);
				String randName = RandomStringUtils.randomAlphabetic(r.nextInt(50));

				SearchableEntityType t = null;
				double rand = r.nextDouble();
				if (rand < 0.5)
					t = SearchableEntityType.HPO;
				else if (rand < 0.8)
					t = SearchableEntityType.Disease;
				else
					t = SearchableEntityType.Gene;

				SearchableEntity entityAlt = new SearchableEntity(randName, randId, t);
				all.add(entityAlt);
			}
		}
	}

	private void addIfNew(List<SearchableEntity> all2, SearchableEntity entity, HashSet<String> alreadyAddedSearchFields) {

		String label = entity.getLabel();
		if (!alreadyAddedSearchFields.contains(label)) {
			alreadyAddedSearchFields.add(label);
			all2.add(entity);
		}

	}

	public List<SearchableEntity> findMatches(String filter, int startIndex, int maxResults) {

		List<SearchableEntity> list = findMatches(filter);
		int last = startIndex + maxResults;
		if (last > list.size()) {
			last = list.size();
		}
		return new ArrayList<SearchableEntity>(list.subList(startIndex, last));
	}

	private List<SearchableEntity> findMatches(String filter) {

		/*
		 * Handle empty or invalid filter
		 */
		if (filter == null || filter.isEmpty()) {
			return all;
		}

		/*
		 * make it lowercase
		 */
		filter = filter.toLowerCase().trim();

		/*
		 * If possible load cached results (works only for one user)
		 */
		if (filter.equals(currentFilter)) {
			return currentResultList;
		}

		ArrayList<SearchableEntity> filtered = new ArrayList<SearchableEntity>();

		String[] filterParts = filter.split("\\s");
		for (SearchableEntity p : all) {
			String pLabel = p.toString().toLowerCase();
			boolean containsAllPartsOfFilter = true;
			for (String filterPart : filterParts) {
				if (!pLabel.contains(filterPart)) {
					containsAllPartsOfFilter = false;
				}
			}
			if (containsAllPartsOfFilter)
				filtered.add(p);
		}

		currentFilter = filter;
		currentResultList = filtered;

		return filtered;
	}

	public int countMatches(String filter) {
		return findMatches(filter).size();
	}

}
