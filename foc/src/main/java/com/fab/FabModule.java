/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.fab;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;

import com.fab.gui.browse.GuiBrowseColumnDesc;
import com.fab.gui.browse.GuiBrowseDesc;
import com.fab.gui.details.GuiDetailsComponentDesc;
import com.fab.gui.details.GuiDetailsDesc;
import com.fab.gui.html.TableHtmlDesc;
import com.fab.gui.xmlView.XMLViewDefinitionDesc;
import com.fab.model.filter.FilterDefinition;
import com.fab.model.filter.FilterDefinitionDesc;
import com.fab.model.filter.FilterDefinitionGuiBrowsePanel;
import com.fab.model.filter.FilterFieldDefinitionDesc;
import com.fab.model.filter.UserDefinedFilterTabbedPanel;
import com.fab.model.menu.MenuDefinition;
import com.fab.model.menu.MenuDefinitionDesc;
import com.fab.model.menu.MenuDefinitionGuiTreePanel;
import com.fab.model.project.FabProject;
import com.fab.model.project.FabProjectDesc;
import com.fab.model.project.FabProjectGuiBrowsePanel;
import com.fab.model.project.FabWorkspaceDesc;
import com.fab.model.project.FabWorkspaceGuiBrowsePanel;
import com.fab.model.table.FabDictionaryGroupDesc;
import com.fab.model.table.FabMultiChoiceSetDesc;
import com.fab.model.table.FabMultiChoiceSetGuiBrowsePanel;
import com.fab.model.table.FabMultipleChoiceDesc;
import com.fab.model.table.FieldDefinitionDesc;
import com.fab.model.table.TableDefinition;
import com.fab.model.table.TableDefinitionDesc;
import com.fab.model.table.TableDefinitionGuiBrowsePanel;
import com.fab.model.table.TableDefinitionGuiTreePanel;
import com.fab.model.table.TableDefinitionTree;
import com.fab.model.table.underlyingCustomisation.UndCustFieldDesc;
import com.fab.model.table.underlyingCustomisation.UndCustTableDesc;
import com.fab.parameterSheet.ParameterSheetSelectorDesc;
import com.foc.Application;
import com.foc.ClassFocDescDeclaration;
import com.foc.Globals;
import com.foc.IFocDescDeclaration;
import com.foc.business.printing.PrnLayoutDefinitionDesc;
import com.foc.desc.FocDesc;
import com.foc.desc.FocModule;
import com.foc.desc.FocObject;
import com.foc.gui.FPanel;
import com.foc.gui.table.view.ViewKeyFactory;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.menu.FMenuAction;
import com.foc.menu.FMenuItem;
import com.foc.menu.FMenuList;

public class FabModule extends FocModule {

	private IFabExtender fabExtender = null;
	private boolean menueAlreadyDeclared = false;

	private static FabModule fabModule = null;

	private ArrayList<Class> arrayOfDescClasses = null;

	private FabModule() {
		arrayOfDescClasses = new ArrayList<Class>();

		arrayOfDescClasses.add(FabWorkspaceDesc.class);
		arrayOfDescClasses.add(FabProjectDesc.class);

		arrayOfDescClasses.add(TableDefinitionDesc.class);
		arrayOfDescClasses.add(FieldDefinitionDesc.class);

		arrayOfDescClasses.add(FabMultipleChoiceDesc.class);
		arrayOfDescClasses.add(FabMultiChoiceSetDesc.class);

		arrayOfDescClasses.add(UndCustTableDesc.class);
		arrayOfDescClasses.add(UndCustFieldDesc.class);
		arrayOfDescClasses.add(MenuDefinitionDesc.class);

		arrayOfDescClasses.add(GuiDetailsDesc.class);
		arrayOfDescClasses.add(GuiDetailsComponentDesc.class);

		arrayOfDescClasses.add(TableHtmlDesc.class);

		arrayOfDescClasses.add(GuiBrowseDesc.class);
		arrayOfDescClasses.add(GuiBrowseColumnDesc.class);
		arrayOfDescClasses.add(FilterDefinitionDesc.class);
		arrayOfDescClasses.add(FilterFieldDefinitionDesc.class);
		arrayOfDescClasses.add(FabDictionaryGroupDesc.class);
		arrayOfDescClasses.add(XMLViewDefinitionDesc.class);
		arrayOfDescClasses.add(PrnLayoutDefinitionDesc.class);

		Globals.getApp().fabDefTables_Add(getTableDefinitionTableStorageName());
		Globals.getApp().fabDefTables_Add(getFieldDefinitionTableStorageName());
		Globals.getApp().fabDefTables_Add(UndCustTableDesc.DB_TABLE_NAME);
		Globals.getApp().fabDefTables_Add(UndCustFieldDesc.DB_TABLE_NAME);
	}

	public int getDBTableCount() {
		return arrayOfDescClasses.size();
	}

	public FocDesc getDBTableAt(int i) {
		Class cls = arrayOfDescClasses.get(i);
		FocDesc focDescription = ClassFocDescDeclaration.getFocDescriptionForClass(cls);
		return focDescription;
	}

	public static FabModule getInstance() {
		if (fabModule == null) {
			fabModule = new FabModule();
		}
		return fabModule;
	}

	@SuppressWarnings("unchecked")
	private void declareUserDefinedTables(Application app) {
		FocLinkSimple tableDefinitionLink = new FocLinkSimple(TableDefinitionDesc.getInstance());
		FocList tablesList = new FocList(tableDefinitionLink, null);

		try {
			if (Globals.getApp().isWebServer()) {
				Globals.getIFocNotification().setNotificationsEnabled(false);
			}
			tablesList.loadIfNotLoadedFromDB();
			Iterator<TableDefinition> iter = tablesList.focObjectIterator();
			while (iter != null && iter.hasNext()) {
				TableDefinition tableDefinition = iter.next();
				IFocDescDeclaration focDescDeclaration = tableDefinition.getDbFocDescDeclaration();
				app.declaredObjectList_DeclareDescription(focDescDeclaration);

				IFocDescDeclaration wfLog_focDescDeclaration = tableDefinition.getDb_WFLog_FocDescDeclaration();
				if (wfLog_focDescDeclaration != null) {
					app.declaredObjectList_DeclareDescription(wfLog_focDescDeclaration);
				}
			}
		} catch (Exception e) {
			Globals.logExceptionWithoutPopup(e);
		} finally {
			if (Globals.getApp().isWebServer() && Globals.getIFocNotification() != null) {
				Globals.getIFocNotification().setNotificationsEnabled(true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void declareUserDefinedFilters(Application app) {
		FocLinkSimple filterDefinitionLink = new FocLinkSimple(FilterDefinitionDesc.getInstance());
		FocList filterDefinitionList = new FocList(filterDefinitionLink);
		try {
			if (Globals.getApp().isWebServer())
				Globals.getIFocNotification().setNotificationsEnabled(false);
			filterDefinitionList.loadIfNotLoadedFromDB();
			Iterator<FilterDefinition> iter = filterDefinitionList.focObjectIterator();
			while (iter != null && iter.hasNext()) {
				FilterDefinition filterDefinition = iter.next();
				FilterFocDescDeclaration focDescDeclaration = FilterFocDescDeclaration
						.getFilterFocDescDeclaration(filterDefinition);
				app.declaredObjectList_DeclareDescription(focDescDeclaration);
			}
		} catch (Exception e) {
			Globals.logExceptionWithoutPopup(e);
		} finally {
			if (Globals.getApp().isWebServer() && Globals.getIFocNotification() != null) {
				Globals.getIFocNotification().setNotificationsEnabled(true);
			}
		}
	}

	private void generateClientCodeForTable(String strTableName) {
		FocDesc focDesc = Globals.getApp().getFocDescByName(strTableName);
		TableDefinition tableDef = TableDefinition.getTableDefinitionForFocDesc(focDesc);

		FabProject fabPrj = FabProjectDesc.getFabProjectByName("basics");
		tableDef.setProject(fabPrj);

		fabPrj = FabProjectDesc.getFabProjectByName("everproWeb");
		tableDef.setProject_WebClient(fabPrj);

		String pkg = tableDef.getPackageName_ServerSide();
		pkg = pkg.substring(4);
		tableDef.setPackageName_WebClient("b01.everpro.web.client.model." + pkg);

		tableDef.writeCode_ClientCode();
		tableDef.validate(true);
	}

	@SuppressWarnings("serial")
	public FMenuList declareMenu(FMenuList rootMenuList) {
		FMenuList manageDataModelList = null;
		if (!menueAlreadyDeclared) {
			manageDataModelList = new FMenuList("Custom Objects", 'a', "FAB_MENU_LIST");

			FMenuItem manageDateModelItem = new FMenuItem("Workspace", 'W', "FAB_WORKSPACE", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					FocList workspaceList = FabWorkspaceDesc.getList(FocList.FORCE_RELOAD);
					FPanel panel = new FabWorkspaceGuiBrowsePanel(workspaceList, FocObject.DEFAULT_VIEW_ID);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageDateModelItem);

			manageDateModelItem = new FMenuItem("Project", 'P', "FAB_PROJECT", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					FocList projectList = FabProjectDesc.getList(FocList.FORCE_RELOAD);
					FPanel panel = new FabProjectGuiBrowsePanel(projectList, FocObject.DEFAULT_VIEW_ID);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageDateModelItem);

			manageDateModelItem = new FMenuItem("Generate Code For FocDesc", 'G', new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					generateClientCodeForTable("ADR_BK_PARTY");
					generateClientCodeForTable("BANK");
				}
			});
			manageDataModelList.addMenu(manageDateModelItem);

			manageDateModelItem = new FMenuItem("Objects / Tables", 'O', "FAB_USER_TABLES", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					FocList list = TableDefinitionDesc.getList(FocList.NONE);
					TableDefinitionTree tree = new TableDefinitionTree(list);
					TableDefinitionGuiTreePanel panel = new TableDefinitionGuiTreePanel(tree,
							FocObject.DEFAULT_VIEW_ID);
					Globals.getDisplayManager().newInternalFrame(panel);

//					FPanel panel = new FocApplicationBuilderGuiDetailsPanel(null,0);
//					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageDateModelItem);

			FMenuItem manageUserMenusItem = new FMenuItem("Menus", 'M', "FAB_USER_MENUS", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					FPanel panel = new MenuDefinitionGuiTreePanel(MenuDefinitionDesc.getList(FocList.LOAD_IF_NEEDED),
							0);
					Globals.getDisplayManager().newInternalFrame(panel);
				}
			});
			manageDataModelList.addMenu(manageUserMenusItem);

			FMenuItem multipleChoiceSetItem = new FMenuItem("Multiple Choice Set", 'M', "FAB_MULTIPLE_CHOICE_SET",
					new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							FPanel panel = new FabMultiChoiceSetGuiBrowsePanel(null, FocObject.DEFAULT_VIEW_ID);
							Globals.getDisplayManager().newInternalFrame(panel);
						}
					});
			manageDataModelList.addMenu(multipleChoiceSetItem);

			FMenuList manageUserFiltersList = new FMenuList("Filters", 'F', "FAB_FILTERS");

			FMenuItem defineUserFiltersItem = new FMenuItem("Filter Structures", 'S', "FAB_FILTER_STRUCTURES",
					new AbstractAction() {
						public void actionPerformed(ActionEvent e) {
							FPanel panel = new FilterDefinitionGuiBrowsePanel(null, FilterDefinition.VIEW_ID_DEFAULT);
							Globals.getDisplayManager().newInternalFrame(panel);
						}
					});
			manageUserFiltersList.addMenu(defineUserFiltersItem);

			FMenuItem createUserFilterItem = new FMenuItem("Filter Instances", 'C', "FAB_FILTER_INSTANCES",
					new AbstractAction() {

						public void actionPerformed(ActionEvent e) {
							FPanel panel = new UserDefinedFilterTabbedPanel();
							Globals.getDisplayManager().newInternalFrame(panel);
						}

					});
			manageUserFiltersList.addMenu(createUserFilterItem);

			manageDataModelList.addMenu(manageUserFiltersList);

			FMenuItem menu = new FMenuItem("Parameter objects / tables", 'P', "FAB_PARAM_SET_TABLES",
					new FMenuAction(ParameterSheetSelectorDesc.getInstance(), true));
			manageDataModelList.addMenu(menu);

			rootMenuList.addMenu(manageDataModelList);
			declareUserDefinedMenus(rootMenuList);
			menueAlreadyDeclared = true;
		}
		return manageDataModelList;
	}

	public static String getTableDefinitionTableStorageName() {
		return "TABLE_DEFINITION";
	}

	public static String getFieldDefinitionTableStorageName() {
		return "FIELD_DEFINITION";
	}

	public void declareUserDefinedMenus(FMenuList rootMenuList) {
		MenuDefinition.fillUserDefinedMenuList(rootMenuList);
	}

	@Override
	public void afterConstruction() {
	}

	@Override
	public void declareFocObjectsOnce() {
		Application app = Globals.getApp();

		for (int i = 0; i < getDBTableCount(); i++) {
			declareFocDescClass(arrayOfDescClasses.get(i));
		}

		TableDefinition.tableDefinitionList_Add(TableDefinitionDesc.getList(FocList.NONE));

		declareUserDefinedTables(app);
		declareUserDefinedFilters(app);

		ViewKeyFactory.getInstance().add(TableDefinitionGuiBrowsePanel.VIEW_KEY_TABLE_DEFINITION,
				TableDefinitionGuiBrowsePanel.VIEW_KEY_TABLE_DEFINITION);
	}

	public IFabExtender getFabExtender() {
		return fabExtender;
	}

	public void setFabExtender(IFabExtender fabExtender) {
		this.fabExtender = fabExtender;
	}
}
