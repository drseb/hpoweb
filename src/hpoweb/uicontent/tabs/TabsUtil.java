/**
 * 
 */
package hpoweb.uicontent.tabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.common.collect.HashMultimap;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.TreeTable;

import hpoweb.data.dataprovider.IEntityDataProvider;
import hpoweb.uicontent.table.HpoClassTableEntry;
import hpoweb.uicontent.table.TableLabel;
import hpoweb.util.CONSTANTS;
import hpoweb.util.UpdatePageClickListener;

/**
 * @author Sebastian Köhler (dr.sebastian.koehler@gmail.com)
 *
 */
public class TabsUtil {

	/**
	 * @param dataProvider
	 * @param tableContent
	 * @return
	 */
	public static TreeTable getTreeTableHpoAnnotations(IEntityDataProvider dataProvider,
			List<HpoClassTableEntry> tableContent) {
		int id;
		TreeTable ttable = new TreeTable();
		ttable.addContainerProperty("HPO id", TableLabel.class, null);
		ttable.addContainerProperty("HPO label", TableLabel.class, null);
		ttable.setSizeFull();
		ttable.setHeight("275px");

		final HashMultimap<OWLClass, OWLClass> leveloneterm2tablecontent = dataProvider.getHpData()
				.getLevelOneBins(tableContent);

		ArrayList<OWLClass> keys = new ArrayList<>(leveloneterm2tablecontent.keySet());
		Collections.sort(keys, new Comparator<OWLClass>() {

			@Override
			public int compare(OWLClass o1, OWLClass o2) {
				if (leveloneterm2tablecontent.get(o1).size() > leveloneterm2tablecontent.get(o2).size())
					return -1;
				if (leveloneterm2tablecontent.get(o1).size() < leveloneterm2tablecontent.get(o2).size())
					return 1;
				return 0;
			}
		});

		id = 0;
		for (OWLClass general : keys) {
			Integer generalItemId = Integer.valueOf(id++);
			String generalId = dataProvider.getHpData().getExtOwlOntology().getOboId(general);
			String generalLabel = dataProvider.getHpData().getExtOwlOntology().getLabel(general.getIRI());

			TableLabel hpoid = new TableLabel(generalId, ContentMode.TEXT);
			TableLabel hpolabel = new TableLabel(generalLabel, ContentMode.TEXT);

			ttable.addItem(new Object[] { hpoid, hpolabel }, generalItemId);

			for (OWLClass annot : leveloneterm2tablecontent.get(general)) {
				Integer annotItemId = Integer.valueOf(id++);
				String annotId = dataProvider.getHpData().getExtOwlOntology().getOboId(annot);
				String annotLabel = dataProvider.getHpData().getExtOwlOntology().getLabel(annot.getIRI());
				TableLabel hpoidAnnot = new TableLabel(annotId, ContentMode.TEXT);
				TableLabel hpolabelAnnot = new TableLabel(annotLabel, ContentMode.TEXT);

				ttable.addItem(new Object[] { hpoidAnnot, hpolabelAnnot }, annotItemId);
				ttable.setParent(annotItemId, generalItemId);
				ttable.setChildrenAllowed(annotItemId, false);

			}

		}

		ttable.addItemClickListener(new UpdatePageClickListener(null, CONSTANTS.hpRequestId));

		return ttable;
	}

}
