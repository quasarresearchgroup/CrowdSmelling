package crowdsmelling.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;



import javax.inject.Inject;
import javax.swing.JFileChooser;

import crowdsmelling.CodeSmellsDetection;
import crowdsmelling.WebServices;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "crowdsmelling.views.SampleView";

	@Inject
	IWorkbench workbench;

	private Table table;
	private TableViewer tableviewer;
	private Action actionConfigPath;
	private Action actionCSvalidate;
	private Action actionCsDetectLongMethod;
	private Action actionCsDetectGodClass;
	private Action actionCsDetectFeatureEnvy;
	private Action actionCsDetectDataClass;
	private Action actionMetricsUpdate;
	private Action doubleClickAction;
	private String filesPath;

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

		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		// table.setLayoutData(new GridData(GridData.FILL_BOTH));

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		tableviewer = new TableViewer(table, SWT.LINE_SOLID | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// tableviewer.setContentProvider(ArrayContentProvider.getInstance());
		// tableviewer.setInput(new String[] { "One", "Two", "Three" });
		// tableviewer.setLabelProvider(new ViewLabelProvider());

		// Create the help context id for the viewer's control
		// workbench.getHelpSystem().setHelp(viewer.getControl(),
		// "crowdSmelling.viewer");
		// getSite().setSelectionProvider(viewer);
		makeActions();
		// hookContextMenu();
		// hookDoubleClickAction();
		contributeToActionBars();

		// **************** Table

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
		csPackage.setWidth(200);
		csClass.setWidth(200);
		csMethod.setWidth(200);
		csCodeSmell.setWidth(100);

		table.pack();
		for (int i = 0; i < 5; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			int j = i + 30;
			item.setText(new String[] { "" + j, "package" + j, "class " + j, "Method " + j, "True" });
		}

		/*
		 * table.addListener(SWT.Selection, new Listener() { public void
		 * handleEvent(Event e) {
		 * 
		 * TableItem item1 = (TableItem) e.item; if (item1.getText(4)=="True") {
		 * item1.setText(4,"False"); } else { item1.setText(4,"True"); }
		 * 
		 * showMessage
		 * (item1.getText(0)+"|"+item1.getText(1)+"|"+item1.getText(2)+"|"+item1.getText
		 * (3)+"|"+item1.getText(4)); } });
		 */

		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Table table = (Table) e.getSource();
				TableItem item1 = (TableItem) table.getItem(table.getSelectionIndex());// e.item;
				if (item1.getText(4) == "True") {
					item1.setText(4, "False");
				} else {
					item1.setText(4, "True");
				}

				showMessage(item1.getText(0) + "|" + item1.getText(1) + "|" + item1.getText(2) + "|" + item1.getText(3)
						+ "|" + item1.getText(4));
			}

			@Override
			public void mouseDown(MouseEvent arg0) {

			}

			@Override
			public void mouseUp(MouseEvent arg0) {

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
		Menu menu = menuMgr.createContextMenu(tableviewer.getControl());
		tableviewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tableviewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionConfigPath);
		manager.add(actionMetricsUpdate);
		manager.add(new Separator());
		manager.add(actionCsDetectLongMethod);
		manager.add(actionCsDetectGodClass);
		manager.add(actionCsDetectFeatureEnvy);
		manager.add(actionCsDetectDataClass);
		manager.add(new Separator());
		manager.add(actionCSvalidate);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(actionConfigPath);
		manager.add(actionMetricsUpdate);
		manager.add(new Separator());
		manager.add(actionCsDetectLongMethod);
		manager.add(actionCsDetectGodClass);
		manager.add(actionCsDetectFeatureEnvy);
		manager.add(actionCsDetectDataClass);
		manager.add(new Separator());
		manager.add(actionCSvalidate);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionConfigPath);
		manager.add(actionMetricsUpdate);
		manager.add(new Separator());
		manager.add(actionCsDetectLongMethod);
		manager.add(actionCsDetectGodClass);
		manager.add(actionCsDetectFeatureEnvy);
		manager.add(actionCsDetectDataClass);
		manager.add(new Separator());
		manager.add(actionCSvalidate);

	}

	private void makeActions() {

		CodeSmellsDetection csDetection = new CodeSmellsDetection();

		// Action Config Path
		actionConfigPath = new Action() {
			public void run() {
				getFilesPath();
			}
		};
		actionConfigPath.setText("Configure path");
		actionConfigPath.setToolTipText("Configure path");
		actionConfigPath.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));

		// Action Update Metrics
		actionMetricsUpdate = new Action() {
			public void run() {
				//get metrics
				showMessage("Update Metrics is end.");
			}
		};
		actionMetricsUpdate.setText("Update Metrics");
		actionMetricsUpdate.setToolTipText("Update Metrics");
		actionMetricsUpdate.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

		// Action detections Long Method
		actionCsDetectLongMethod = new Action() {
			public void run() {
				try {
					csDetection.classifyData("LongMethod.model", "structure-method.arff");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMessage("Long Method Detections is end.");
			}
		};
		actionCsDetectLongMethod.setText("Long Method Detection");
		actionCsDetectLongMethod.setToolTipText("Long Method Detection");
		actionCsDetectLongMethod.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OPEN_MARKER));

		// Action detections God Class
		actionCsDetectGodClass = new Action() {
			public void run() {
				try {
					csDetection.classifyData("GodClass.model", "structure-class.arff");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMessage("God Class Detections is end.");
			}
		};
		actionCsDetectGodClass.setText("God Class Detection");
		actionCsDetectGodClass.setToolTipText("God Class Detection");
		actionCsDetectGodClass.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_BKMRK_TSK));

		// Action detections Feature Envy
		actionCsDetectFeatureEnvy = new Action() {
			public void run() {
				try {
					csDetection.classifyData("FeatureEnvy.model", "structure-class.arff");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMessage("Feature Envy Detections is end.");
			}
		};
		actionCsDetectFeatureEnvy.setText("Feature Envy Detection");
		actionCsDetectFeatureEnvy.setToolTipText("Feature Envy Detection");
		actionCsDetectFeatureEnvy.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		// Action detections Data Class
		actionCsDetectDataClass = new Action() {
			public void run() {
				try {
					csDetection.classifyData("DataClass.model", "structure-class.arff");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMessage("Data Class Detections is end.");
			}
		};
		actionCsDetectDataClass.setText("Data Class Detection");
		actionCsDetectDataClass.setToolTipText("Data Class Detection");
		actionCsDetectDataClass.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));

		// Action CS validation
		actionCSvalidate = new Action() {
			public void run() {
				TableItem item;
				byte iscodesmell;
				int id;

				for (int i = 0; i < table.getItemCount(); i++) {
					item = (TableItem) table.getItem(i);
					if (item.getText(4) == "True") {
						iscodesmell = 1;
					} else {
						iscodesmell = 0;
					}
					id = Integer.parseInt(item.getText(0));
					showMessage("id->" + id + " | isCS-> " + iscodesmell);
					WebServices webservices = new WebServices();
					webservices.writePut2Mysql(id, iscodesmell);
				}
				showMessage("Code Smells Validation is end.");
			}
		};
		actionCSvalidate.setText("Validate Results");
		actionCSvalidate.setToolTipText("Validate Results");
		actionCSvalidate
				.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_TASK_TSK));

	}
	
	
	private void getFilesPath() {
		Shell shell = new Shell (Display.getCurrent());
		FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		String[] filterExt = { "*.model;*.arff", "*.*" };
		dialog.setFilterExtensions(filterExt);
		dialog.setFilterPath("C:\\Java\\Git\\CrowdSmellingUpdateSite\\crowdsmellingUpdatesite\\models\\qwer");
		dialog.open();
		filesPath = dialog.getFilterPath();	
	}
	
	
	private void hookDoubleClickAction() {
		tableviewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(tableviewer.getControl().getShell(), "CrowdSmelling View", message);
	}

	@Override
	public void setFocus() {
		tableviewer.getControl().setFocus();
	}
}
