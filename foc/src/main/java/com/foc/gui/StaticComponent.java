/*
 * Created on Nov 28, 2005
 */
package com.foc.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.RootPaneContainer;

import com.foc.Globals;

import java.awt.*;

/**
 * @author 01Barmaja
 */
public class StaticComponent {
	
  private static void setEnabled(boolean setBackgroundColor, Component comp, boolean editable, boolean output){
  	if(!editable || output){
      if(setBackgroundColor){
      	comp.setBackground(new Color(235, 235, 235));
      }
      comp.setForeground(Globals.getDisplayManager().getNotEditableForeground());
      if(output){
        comp.setFont(Globals.getDisplayManager().getDefaultOutputFont());
      }else{
        comp.setFont(Globals.getDisplayManager().getDefaultNotEditableFont());      	
      }
  	}else{
  		comp.setForeground(Globals.getDisplayManager().getEditableForeground());
      comp.setFont(Globals.getDisplayManager().getDefaultFont());
      if(setBackgroundColor) comp.setBackground(Globals.getDisplayManager().getEditableBackground());
  	}
  }

  public static void setEnabled(Component comp, boolean b, boolean output){
    setEnabled(true, comp, b, output);
  }
  
  public static void setEnabledNoBackground(Component comp, boolean b, boolean output){
    setEnabled(false, comp, b, output);
  }
  
  public static void cleanComponent(Component baseComponent){
    try{
      cleanComponent(baseComponent, 0);
    }
    catch ( Exception e ){
      Globals.logException(e);
    }
  }
  
  public static void setAllComponentsEnable(Component baseComponent, boolean enable, boolean recursive){
  	if(baseComponent != null){
  		if(baseComponent instanceof FValidationPanel || baseComponent instanceof JLabel){
  			
  		}else if(baseComponent instanceof FGTextAreaPanel){
  			((FGTextAreaPanel)baseComponent).getTextArea().setEnabled(enable);
  			setEnabled(((FGTextAreaPanel)baseComponent).getTextArea(), enable, false);
  		}else if(baseComponent instanceof JPanel || baseComponent instanceof JTabbedPane || baseComponent instanceof JSplitPane || baseComponent instanceof JScrollPane){
  			if(recursive){
	  			Container container = (Container) baseComponent;
		    	Component comps[] = container.getComponents();
		    	for(int i = 0; i < comps.length; i++){
		    		Component component = comps[i];
		    		if(component != null){
		    			setAllComponentsEnable(component, enable, recursive);
		    		}
		    	}
  			}
  		}else{
  			baseComponent.setEnabled(enable);
  			setEnabled(baseComponent, enable, false);
  		}
  	}
  }
  
  /*
  * The "depth" parameter was being used for text output debugging.
  * But isn't essential now.  I'll keep it anyways, as it avoids
  * calling the garbage collector every recursion.
  */
  protected static void cleanComponent(Component baseComponent, int depth){
    if (baseComponent == null){ // recursion terminating clause
      return ;
    }
 
    Container   cont;
    Component[] childComponents;
    int         numChildren;
 
    if(baseComponent instanceof FPanel){
      FPanel panel = (FPanel) baseComponent;
      panel.dispose();
    }else if(baseComponent instanceof FGButton){
      FGButton fComp = (FGButton) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGToggleButton){
      FGToggleButton fComp = (FGToggleButton) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGAbstractComboBox){
      FGAbstractComboBox fComp = (FGAbstractComboBox) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGCheckBox){
      FGCheckBox fComp = (FGCheckBox) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGFormattedTextField){
      FGFormattedTextField fComp = (FGFormattedTextField) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGLabel){
      FGLabel fComp = (FGLabel) baseComponent;
      fComp.dispose();      
    }else if(baseComponent instanceof FGTextField){
      FGTextField fComp = (FGTextField) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGPasswordField){
      FGPasswordField fComp = (FGPasswordField) baseComponent;
      fComp.dispose();
    }else if(baseComponent instanceof FGDateChooser){
    	FGDateChooser fComp = (FGDateChooser) baseComponent;
      fComp.dispose();
    }
    
    // clean up component containers
    if(baseComponent instanceof Container){
      // now clean up container instance variables
      if(baseComponent instanceof RootPaneContainer){ // Swing specialised container
        cont = (Container)baseComponent;
        numChildren = cont.getComponentCount();
        childComponents = cont.getComponents();
        for(int i = 0;i < numChildren;i++){
          // remove each component from the current container
          // each child component may be a container itself
          cleanComponent(childComponents[i], depth + 1);
          ((RootPaneContainer)cont).getContentPane().remove(childComponents[i]);
        }
        ((RootPaneContainer)cont).getContentPane().setLayout(null);
      }else{ // General Swing, and AWT, Containers
        cont = (Container)baseComponent;
        numChildren = cont.getComponentCount();
 
        childComponents = cont.getComponents();
        for(int i = 0;i < numChildren;i++){  //for(int i = 0;i < numChildren;i++)
          // remove each component from the current container
          // each child component may be a container itself
          cleanComponent(childComponents[i], depth + 1);
          cont.remove(childComponents[i]);
        }
        cont.setLayout(null);
      }      
    } // if component is also a container
  }
  
  public static void setComponentToolTipText(JComponent jComp, String toolTipText){
  	if(Globals.getApp() != null && Globals.getApp().getUser() != null && Globals.getApp().getUser().isEnableToolTipText()){
  		jComp.setToolTipText(toolTipText);
  	}
  }
}
