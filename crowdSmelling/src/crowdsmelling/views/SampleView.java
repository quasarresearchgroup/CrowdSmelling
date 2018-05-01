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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.inject.Inject;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.xml.bind.ParseConversionEvent;

/*import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.CSVReaderBuilder;*/
import org.eclipse.e4.core.services.events.IEventBroker;
import crowdsmelling.CodeSmellsDetection;
import crowdsmelling.WebServices;
import iscte.analytics.plugin.eclipse.events.InternalEventsBusBroker;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

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
	private String filesPath, username;

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
		csPackage.setWidth(250);
		csClass.setWidth(200);
		csMethod.setWidth(200);
		csCodeSmell.setWidth(100);
		table.pack();
/*		
		for (int i = 0; i < 5; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			int j = i + 30;
			item.setText(new String[] { "" + j, "package" + j, "class " + j, "Method " + j, "True" });
		}

		
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
				if (item1.getText(4) == "true") {
					item1.setText(4, "false");
				} else {
					item1.setText(4, "true");
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
		IEventBroker eventBusBroker = new InternalEventsBusBroker().getBroker();

		// Action Config Path
		actionConfigPath = new Action() {
			public void run() {
				// IEventBroker eventBusBroker JCaldeira;
				eventBusBroker.post("iscte/analytics/plugin/events/smells", "crowdConfigurations");

				// Get files models path
				getFilesPath();
				username = JOptionPane.showInputDialog(null, "Enter your username?", "CrowdSmelling",
						JOptionPane.INFORMATION_MESSAGE);
				
				//Enable actions
				actionMetricsUpdate.setEnabled(true);
				actionCsDetectLongMethod.setEnabled(true);
				actionCsDetectGodClass.setEnabled(true);
				actionCsDetectFeatureEnvy.setEnabled(true);
				actionCsDetectDataClass.setEnabled(true);
			}
		};
		actionConfigPath.setText("Configure path");
		actionConfigPath.setToolTipText("Configure path");
		actionConfigPath.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		
		
		// Action Update Metrics
		actionMetricsUpdate = new Action() {
			public void run() {
				// get metrics
				showMessage("Update Metrics is end.");
				eventBusBroker.post("iscte/analytics/plugin/events/smells", "updateMetrics");
			}
		};
		actionMetricsUpdate.setText("Update Metrics");
		actionMetricsUpdate.setToolTipText("Update Metrics");
		actionMetricsUpdate.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		actionMetricsUpdate.setEnabled(false);
		
		// Action detections Long Method
		actionCsDetectLongMethod = new Action() {
			public void run() {
				    
				try {
					//get model
					Object[] model = weka.core.SerializationHelper.readAll(filesPath+"\\LongMethod.model");
					System.out.println("model name: "+ model[0].getClass().getTypeName()); 
					Classifier modelClassifier = (Classifier) model[0];    //Get model
					
					// read file with metrics
					CSVLoader loader = new CSVLoader();
					loader.setSource(new File("C:\\Java\\eclipse-workspace\\CrowdSmelling-Information\\DataSets\\long-method.csv"));
					// get instances in CSV
					Instances data = loader.getDataSet();
					
					boolean iscodesmell;
					table.removeAll();
					WebServices webservices = new WebServices();
					for (int i = 0; i < data.numInstances(); i++) {
						
						//csDetection.classifyData(data.instance(i) , "Long Method", username, filesPath, "LongMethod.model", "structure-method.arff");
						
						data.setClassIndex(data.numAttributes()-1);	
						System.out.println("dataIst->"+data.instance(i));
							
						double actualClassValue = data.instance(i).classValue();
						//call classifyInstance, which returns a double value for the class
						double predClassValue = modelClassifier.classifyInstance(data.instance(i));
						System.out.println("Classification: "+actualClassValue+", "+predClassValue);
						
						if (predClassValue==1){ //data.get(i).value(data.numAttributes()-1)==1){
							iscodesmell=true;
						} else{
							iscodesmell=false;
						};
						
						//Insert POST DB
						String methodName=data.get(i).toString(4); //remove char"'" from csv
						if(data.get(i).toString(4).startsWith("'")) {
							methodName=data.get(i).toString(4).replace("'","");
						}
						
						String idRowDb= webservices.writePost2Mysql(username,"Long Method",model[0].getClass().getTypeName(),data.get(i).toString(1), data.get(i).toString(2),data.get(i).toString(3),methodName,data.get(i).value(11),data.get(i).value(12),iscodesmell,iscodesmell);
						// insert data in table
						TableItem item = new TableItem(table, SWT.NONE); 
						item.setText(new String[] {idRowDb.trim(),data.get(i).toString(2), data.get(i).toString(3),methodName,String.valueOf(iscodesmell)});
						
/*		 				for (int a = 0; a < data.numAttributes(); a++) {
							System.out.println(data.get(i).toString(a)); 
						}
 */						
						//Enable actions validate
						actionCSvalidate.setEnabled(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				eventBusBroker.post("iscte/analytics/plugin/events/smells", "longMethod");

				showMessage("Long Method Detections is end.");
			}
		};
		actionCsDetectLongMethod.setText("Long Method Detection");
		actionCsDetectLongMethod.setToolTipText("Long Method Detection");
		actionCsDetectLongMethod.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OPEN_MARKER));
		actionCsDetectLongMethod.setEnabled(false);
		
		// Action detections God Class
		actionCsDetectGodClass = new Action() {
			public void run() {
				try {
					// csDetection.classifyData("God Class", username, filesPath, "GodClass.model",
					// "structure-class.arff");
					eventBusBroker.post("iscte/analytics/plugin/events/smells", "godClass");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Enable actions validate
				actionCSvalidate.setEnabled(true);
				
				showMessage("God Class Detections is end.");
			}
		};
		actionCsDetectGodClass.setText("God Class Detection");
		actionCsDetectGodClass.setToolTipText("God Class Detection");
		actionCsDetectGodClass.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_BKMRK_TSK));
		actionCsDetectGodClass.setEnabled(false);
		
		// Action detections Feature Envy
		actionCsDetectFeatureEnvy = new Action() {
			public void run() {
				try {
					// csDetection.classifyData("Feature Envy", username, filesPath,
					// "FeatureEnvy.model", "structure-method.arff");
					eventBusBroker.post("iscte/analytics/plugin/events/smells", "featureEnvy");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Enable actions validate
				actionCSvalidate.setEnabled(true);
				
				showMessage("Feature Envy Detections is end.");
			}
		};
		actionCsDetectFeatureEnvy.setText("Feature Envy Detection");
		actionCsDetectFeatureEnvy.setToolTipText("Feature Envy Detection");
		actionCsDetectFeatureEnvy.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		actionCsDetectFeatureEnvy.setEnabled(false);
		
		// Action detections Data Class
		actionCsDetectDataClass = new Action() {
			public void run() {
				try {
					// csDetection.classifyData("Data Class", username, filesPath,
					// "DataClass.model", "structure-class.arff");
					eventBusBroker.post("iscte/analytics/plugin/events/smells", "dataClass");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//Enable actions validate
				actionCSvalidate.setEnabled(true);
				
				showMessage("Data Class Detections is end.");
			}
		};
		actionCsDetectDataClass.setText("Data Class Detection");
		actionCsDetectDataClass.setToolTipText("Data Class Detection");
		actionCsDetectDataClass.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		actionCsDetectDataClass.setEnabled(false);
		
		// Action CS validation
		actionCSvalidate = new Action() {
			public void run() {
				TableItem item;
				byte iscodesmell;
				WebServices webservices = new WebServices();

				for (int i = 0; i < table.getItemCount(); i++) {
					item = (TableItem) table.getItem(i);
					if (item.getText(4) == "true") {
						iscodesmell = 1;
					} else {
						iscodesmell = 0;
					}
					int idRow = Integer.parseInt(item.getText(0));
					//showMessage("id->" + idRow + " | isCS-> " + iscodesmell);

					//Update DB
					webservices.writePut2Mysql(idRow, iscodesmell);
				}
				showMessage("Code Smells Validation is end.");
				eventBusBroker.post("iscte/analytics/plugin/events/smells", "validationCodeSmell");
			}
		};
		actionCSvalidate.setText("Validate Results");
		actionCSvalidate.setToolTipText("Validate Results");
		actionCSvalidate
				.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_TASK_TSK));
		actionCSvalidate.setEnabled(false);
	}

	private void getFilesPath() {
		Shell shell = new Shell(Display.getCurrent());
		FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
		String[] filterExt = { "*.model;*.arff", "*.*" };
		dialog.setFilterExtensions(filterExt);
		dialog.setFilterPath("C:\\Java\\Git\\CrowdSmellingUpdateSite\\models\\");
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
