package hpoweb.data.dataprovider;

/**
 * Provide data for an entity like an HPO class or a gene
 * 
 * @author Sebastian Koehler
 *
 */
public interface IEntityDataProvider {

	public String getId();

	public String getLabel();

	public String getTypeOfEntityString();

}
