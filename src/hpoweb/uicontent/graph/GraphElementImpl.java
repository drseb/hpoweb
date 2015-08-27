package hpoweb.uicontent.graph;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.graph.GraphElement;


class GraphElementImpl implements GraphElement {

  private final String id;
  private final String label;
  private Map<String, Object> properties;

  public GraphElementImpl(String id) {
    this(id, id);
  }

  public GraphElementImpl(String id, String label) {
    super();
    this.id = id;
    this.label = label;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public Map<String, Object> getProperties() {
    if (properties == null) {
      properties = new HashMap<String, Object>();
    }
    return properties;
  }
  
  public void setStyle(String style) {
	getProperties().put(GraphElement.PROPERTY_NAME_STYLE, style);
  }

}

