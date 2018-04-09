package crowdsmelling.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import javax.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "crowdsmelling.views.SampleView";

	@Inject IWorkbench workbench;
	
	private Table table;
	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	 

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		//viewer.setContentProvider(ArrayContentProvider.getInstance());
		//viewer.setInput(new String[] { "One", "Two", "Three" });
		//viewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		//workbench.getHelpSystem().setHelp(viewer.getControl(), "crowdSmelling.viewer");
		//getSite().setSelectionProvider(viewer);
		makeActions();
		//hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();
		
		
		
		//**************** Table   
	    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
	    //table.setLayoutData(new GridData(GridData.FILL_BOTH));

	    table.setLinesVisible(true);
	    table.setHeaderVisible(true);
	    
	    TableColumn csID = new TableColumn(table, SWT.LEFT);
	    csID.setText("ID");
	    
	    TableColumn csPackage = new TableColumn(table, SWT.LEFT);
	    csPackage.setText("Package");

	    TableColumn csClass = new TableColumn(table, SWT.NULL);
	    csClass.setText("Class");

	    TableColumn csMethod = new TableColumn(table, SWT.NULL);
	    csMethod.setText("Method");

	    TableColumn csCodeSmell = new TableColumn(table, SWT.NULL);
	    csCodeSmell.setText("Code Smell?");

	    csID.setWidth(50);
	    csPackage.setWidth(100);
	    csClass.setWidth(100);
	    csMethod.setWidth(100);
	    csCodeSmell.setWidth(100);
	    
	    table.pack(); 
	    for (int i = 0; i < 5; i++) {
	    	TableItem item=new TableItem(table, SWT.NONE);
	        item.setText(new String[] {""+i, "package" + i, "class " + i,"Method " + i,"True"});
	    }
	    
	    table.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	        	//showMessage("event"+ e.type);
/*		      String string = "";
	          TableItem[] selection = table.getSelection();
	          for (int i = 0; i < selection.length; i++)
	            string += selection[i] + " ";
	          showMessage("Selection={" + string + "} "+ e.item + " | "+table.getSelectionIndex() );
          TableItem item = new TableItem(table, SWT.NONE, table.getSelectionIndex());
	          //item.setText(" New Item ");
	          showMessage("AAA"+ item.getText(2));*/
	          
	          TableItem item1 = (TableItem) e.item;
	          int index = table.indexOf (item1);
	          if (item1.getText(4)=="True") {
	        	 item1.setText(4,"False"); 
	          }
	          else {
	        	  item1.setText(4,"True"); 
	          }
	          
	          showMessage (item1.getText(0)+"|"+item1.getText(1)+"|"+item1.getText(2)+"|"+item1.getText(3)+"|"+item1.getText(4));
	        }
	    });
		
	}

	
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SampleView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Code Smells Detections is end.");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Code Smell Detection");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OPEN_MARKER));
		
		action2 = new Action() {
			public void run() {
				showMessage("Code Smells Validation is end.");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Validate Results");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_TASK_TSK));
		doubleClickAction = new Action() {
			public void run() {
				//IStructuredSelection selection = viewer.getStructuredSelection();
				//Object obj = selection.getFirstElement();
				showMessage("Double-click detected on "); //+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"CrowdSmelling View",
			message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
