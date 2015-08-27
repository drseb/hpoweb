package hpoweb;

import javax.servlet.ServletException;

import com.vaadin.server.VaadinServlet;

@SuppressWarnings("serial")
public class MyVaadinServlet extends VaadinServlet {

	@Override
	protected void servletInitialized() throws ServletException {
		getService().addSessionInitListener(new HpowebSessionInitListener());
	}

}
