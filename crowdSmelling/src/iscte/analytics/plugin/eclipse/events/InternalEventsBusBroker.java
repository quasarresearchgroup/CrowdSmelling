package iscte.analytics.plugin.eclipse.events;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;

public class InternalEventsBusBroker {
	
	public InternalEventsBusBroker() {
		// TODO Auto-generated constructor stub
	}

	public IEventBroker getBroker() {
			
	 Object service = PlatformUI.getWorkbench().getService(IEventBroker.class);  
	 if (service instanceof IEventBroker) {  
		 
		 return (IEventBroker) service;
		 
	 }
	return null;  
 
	}
	 
}


