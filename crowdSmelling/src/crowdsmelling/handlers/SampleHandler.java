package crowdsmelling.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import crowdsmelling.CodeSmellsDetection;

//import iscte.analytics.plugin.eclipse.events.InternalEventsBusBroker;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		CodeSmellsDetection CSDetection=new CodeSmellsDetection();

		if(event.getCommand().getId().equals("crowdSmelling.commands.codeMetrics")) {
			CSDetection.Metrics();
		} 
		else if(event.getCommand().getId().equals("crowdSmelling.commands.longMethod")) {
			try {
				CSDetection.longMethod();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(event.getCommand().getId().equals("crowdSmelling.commands.godClass")) {
			try {
				CSDetection.godClass();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		else if(event.getCommand().getId().equals("crowdSmelling.commands.featureEnvy")) {
			CSDetection.featureEnvy();
		}
		else if(event.getCommand().getId().equals("crowdSmelling.commands.dataClass")) {
			CSDetection.dataClass();
		}		
		else if(event.getCommand().getId().equals("crowdSmelling.commands.informationPage")) {
			CSDetection.informationPage();
		}

/*		//define a variavel para  
		//IEventBroker eventBusBroker; 
		// Aqui ja estas a usar a class que te envio em attach
		IEventBroker eventBusBroker =  new InternalEventsBusBroker().getBroker(); 
		// aqui envias o evento para o canal "iscte/analytics/plugin/eventsâ€� onde eu estou a escuta, pode ser um objecto json { â€œnameâ€� : â€œClass Selectionâ€�, â€œtimestampâ€�: â€œxyzâ€� } em vez de uma string
		eventBusBroker.post("iscte/analytics/plugin/events", event); 
*/
		return null;
	}
}
