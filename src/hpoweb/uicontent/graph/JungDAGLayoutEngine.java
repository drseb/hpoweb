package hpoweb.uicontent.graph;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.collections15.Transformer;

import com.vaadin.graph.layout.JungLayoutEngine;
import com.vaadin.graph.layout.JungLayoutEngineModel;
import com.vaadin.graph.shared.ArcProxy;
import com.vaadin.graph.shared.NodeProxy;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

public class JungDAGLayoutEngine extends JungLayoutEngine {
	private static final long serialVersionUID = 1L;
	private JungLayoutEngineModel model;

	public JungDAGLayoutEngine() {
		this(new JungLayoutEngineModel());
	}

	public JungDAGLayoutEngine(JungLayoutEngineModel model) {

		super(model);
		this.model = model;
	}

	@Override
	public void layout(final int width, final int height, Collection<NodeProxy> lockedNodes) {

		AbstractLayout<NodeProxy, ArcProxy> layout1 = createLayout(model.getGraph(), new Dimension(width, height));
		layoutWithLayout(width, height, lockedNodes, layout1, 5000);

		// int numberOfNodes = model.getNodes().size();
		// AbstractLayout<NodeProxy, ArcProxy> layout2 = createLayout2(model.getGraph(), new Dimension(width, height));
		// layoutWithLayout(width, height, lockedNodes, layout2, numberOfNodes);
	}

	private void layoutWithLayout(final int width, final int height, Collection<NodeProxy> lockedNodes, AbstractLayout<NodeProxy, ArcProxy> layout,
			int numSteps) {

		layout.lock(false);
		for (NodeProxy v : lockedNodes) {
			layout.lock(v, true);
		}
		layout.setInitializer(new Transformer<NodeProxy, Point2D>() {
			public Point2D transform(NodeProxy input) {
				int x = input.getX();
				int y = input.getY();
				return new Point2D.Double(x == -1 ? new Random().nextInt(width) : x, y == -1 ? new Random().nextInt(height) : y);
			}
		});

		layout.initialize();
		int iterationCounter = 0;
		if (layout instanceof IterativeContext) {
			IterativeContext layoutIt = ((IterativeContext) layout);
			while (!layoutIt.done()) {
				iterationCounter++;
				layoutIt.step();
				/*
				 * Sometimes this seems to cause problems and the .done() does not terminate. Thus we make a hard stop after a fixed number
				 * of iterations
				 */
				if (iterationCounter > numSteps)
					break;
			}
		}

		for (NodeProxy v : model.getGraph().getVertices()) {
			Point2D location = layout.transform(v);
			v.setX((int) location.getX());
			v.setY((int) location.getY());
		}
	}

	@Override
	protected AbstractLayout<NodeProxy, ArcProxy> createLayout(Graph<NodeProxy, ArcProxy> graph, Dimension size) {
		// FRLayout2<NodeProxy, ArcProxy> layout = new FRLayout2<NodeProxy,
		// ArcProxy>(graph);
		DAGLayout<NodeProxy, ArcProxy> layout = new DAGLayout<NodeProxy, ArcProxy>(graph);
		// Dimension d = new Dimension(2000, 2000);
		layout.setSize(size);
		return layout;
	}

	protected AbstractLayout<NodeProxy, ArcProxy> createLayout2(Graph<NodeProxy, ArcProxy> graph, Dimension size) {
		FRLayout2<NodeProxy, ArcProxy> layout = new FRLayout2<NodeProxy, ArcProxy>(graph);
		// DAGLayout<NodeProxy, ArcProxy> layout = new DAGLayout<NodeProxy,
		// ArcProxy>(graph);
		// Dimension d = new Dimension(2000, 2000);
		layout.setSize(size);
		return layout;
	}

}
