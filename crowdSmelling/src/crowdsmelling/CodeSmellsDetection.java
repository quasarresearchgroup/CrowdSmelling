package crowdsmelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.json.simple.JSONObject;
import weka.classifiers.Classifier;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instance;
import weka.core.Instances;


public class CodeSmellsDetection {
	
	public void Metrics() {
		MessageDialog.openInformation(null,"CrowdSmelling Code Metrics","Activate the view:\n" + 
						"Windows -> Show View -> Other -> CrowdSmelling->CrowdSmelling Detections");
		
	}	
	public void longMethod() throws Exception {
		//classifyData("LongMethod.model", "structure-method.arff");
		MessageDialog.openInformation(null,"CrowdSmelling detection Long Method","Activate the view:\n" + 
						"Windows -> Show View -> Other -> CrowdSmelling->CrowdSmelling Detections");
/*
        System.out.println(fileName);
       
        System.out.println( this.getClass().getResource("/LongMethod.model"));
        System.out.println(this.getClass().getClassLoader().getResource(fileName)); 
       	System.out.println(this.getClass().getClassLoader().getResource("/LongMethod.model"));
     
		
		System.out.println(this.getClass().getClassLoader().getResource("/models/LongMethod.model"));
                
        URL fullPathfileModel = this.getClass().getClassLoader().getResource("/models/LongMethod.model");
        System.out.println(fullPathfileModel.getPath());
        System.out.println(fullPathfileModel.getFile());
        
        //Path path= Paths.get(fullPathfileModel);
        
        File f =new File(fullPathfileModel.getPath()); 
        String ficheiro= f.toPath().toString(); 
        System.out.println(ficheiro);
        
        MessageDialog.openInformation(null,"ficheiro","ficheiro \n"+ ficheiro);
    	Object[] model = weka.core.SerializationHelper.readAll(ficheiro);
    	System.out.println("model name: "+ model[0].getClass().getTypeName());
    	MessageDialog.openInformation(null,"MODEL","MODEL \n"+ model[0].getClass().getTypeName());
    	
    	if (Files.exists(f.toPath())) {
		      // file exist 
		    	//Object[] model = weka.core.SerializationHelper.readAll(ficheiro);
			//get model
		    System.out.println("model name: "+ model[0].getClass().getTypeName()); 
		    
    	}
    	else {
			MessageDialog.openInformation(null,"ERROR"," File not found:\n"+ fullPathfileModel);
		}*/
        

	}
	
	public void godClass() throws Exception {
		//classifyData("GodClass.model","structure-class.arff");
		MessageDialog.openInformation(null,"CrowdSmelling detection God Class","Activate the view:\n" +
						"Windows -> Show View -> Other -> CrowdSmelling->CrowdSmelling Detections");
	}
	
	
	public void featureEnvy() {
		MessageDialog.openInformation(null,"CrowdSmelling detection Feature Envy","Activate the view:\n" + 
						"Windows -> Show View -> Other -> CrowdSmelling->CrowdSmelling Detections");
		
	}
	
	public void dataClass() {
		MessageDialog.openInformation(null,"CrowdSmelling detection Data Class","Activate the view:\n" + 
						"Windows -> Show View -> Other -> CrowdSmelling->CrowdSmelling Detections");
		
	}
	
	public void informationPage() {
		String info="Version: 0.0.1\n\n"+"Authors: José Reis\n"+"             Luís Prates\n"+"             FBA\n\n"+"www.crowdsmelling.com";
		MessageDialog.openInformation(null,"CrowdSmelling Information",info);	
	}
	
	public void classifyData(Instance dataInstance, String codeSmellType, String username, String filesPath, String fileModel, String instanceStructure) throws Exception {
	//Classifier modelClassifier = null;
		
/*//		Properties prop=new Properties();
//		String fullPathFile=prop.getProperty("models")+ fileModel;
//		prop.load (this.getClass().getClassLoader().getResourceAsStream("/conf/crowd.properties"));	
		
		//Get path to classifier model
		//String workSpaceRootpath=ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		//String workSpaceRootpath=this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		//String fullPath = workSpaceRootpath.replaceFirst("/","" );		
		//fullPath = fullPath.replace("/", "\\");	
		
		URL fullPath = this.getClass().getClassLoader().getResource("/CrowdSmelling/models/"+fileModel);	
		System.out.println("URL->" + fullPath+" | URLPath->"+fullPath.getPath());
		String fullPathfileModel = fullPath + "models\\" +  fileModel;
			
		fullPathfileModel="C:\\Java\\Git\\CrowdSmelling\\models\\" + fileModel;
		fullPathfileModel="\\models\\" + fileModel;
		System.out.println("fullPathfileModel ->"+ fullPathfileModel);*/
		
		
		Path path= Paths.get(filesPath);
		if (Files.exists(path)) {
		      // file exist 
		    Object[] model = weka.core.SerializationHelper.readAll(filesPath+"\\"+fileModel);
			//get model
		    System.out.println("model name: "+ model[0].getClass().getTypeName()); 
		    
		    //modelClassifier = AbstractClassifier.forName(model[0].getClass().getTypeName(), null); 
		    //modelClassifier = (AbstractClassifier) model[0];
		    
		    Classifier modelClassifier = (Classifier) model[0];    //Get model 
			//System.out.println("model 0: "+ model[0]);
			
			//get instances - attributes
			Instances headers = (Instances) model[1];
			//System.out.println("model 1: "+headers);
			System.out.println("headersnum: "+headers.numAttributes());

			System.out.println("::::::::::::Classificar on fly::::::::::::::::::::::::::::::");
			
			//load new dataset			
			//String fullPathinstanceStructure = fullPath + "models\\" +  instanceStructure;
			//DataSource structureClass = new DataSource(fullPathinstanceStructure);
			
			DataSource structureClass = new DataSource(filesPath+"\\"+instanceStructure);
			Instances csInstances = structureClass.getDataSet();
			
			//set class index to the last attribute	    
			csInstances.setClassIndex(csInstances.numAttributes()-1);
			
			csInstances.instance(0).setValue(headers.attribute(0),dataInstance.value(0));
/*			System.out.println("0->"+csInstances.instance(0).value(0)+ " | " +  dataInstance.value(0));
			csInstances.instance(0).setValue(headers.attribute(1),dataInstance.toString(1));
			System.out.println("1->"+csInstances.instance(0).toString(1)+ " | " +  dataInstance.toString(1));
			csInstances.instance(0).setValue(headers.attribute(2),dataInstance.toString(2));
			System.out.println("2->"+csInstances.instance(0).toString(2)+ " | " +  dataInstance.toString(2));
			csInstances.instance(0).setValue(headers.attribute(3),dataInstance.toString(3));
			System.out.println("3->"+csInstances.instance(0).toString(3)+ " | " +  dataInstance.toString(3));
			csInstances.instance(0).setValue(headers.attribute(4),dataInstance.toString(4));
			System.out.println("4->"+csInstances.instance(0).toString(4)+ " | " +  dataInstance.toString(4));
*/			
			for(int i=5; i<headers.numAttributes()-1; i++){
				//Set attribute - ONLY ONE INSTANCE EXISTES - FIRSTS ATTRIBUTES NOT NUMBERS????????
				csInstances.instance(0).setValue(headers.attribute(i),dataInstance.value(i));
			}	
			
			if(dataInstance.toString(headers.numAttributes()-1)=="1") {
				csInstances.instance(0).setValue(headers.attribute(headers.numAttributes()-1),"True");
			System.out.println("dataIst->"+dataInstance);
			System.out.println("Insta->"+csInstances.instance(0));	
			}

			//get class double value for first instance
			double actualClassValue = csInstances.instance(0).classValue();
			//call classifyInstance, which returns a double value for the class
		
			double predClassValue = modelClassifier.classifyInstance(csInstances.instance(0));

			System.out.println("Classification: "+actualClassValue+", "+predClassValue);
			
			//Write DB
			//writeDBpost2Mysql(model[0].getClass().getTypeName(), fileModel);
			//MessageDialog.openInformation(null,"CrowdSmelling","Code Smells detection complete.");
		}
		else {
			MessageDialog.openInformation(null,"ERROR"," File not found:\n"+ filesPath);
		}
	}
	
	
}
