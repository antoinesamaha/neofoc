/*
 * Created on 19-May-2005
 */
package com.foc.menu;

import com.foc.desc.*;
import com.foc.gui.*;
import com.foc.list.FocList;

/**
 * @author 01Barmaja
 */
@SuppressWarnings("serial")
public class FMenuAction extends FAbstractMenuAction {
	public FMenuAction(FocDesc focDesc, FocList list, boolean uniquePopup) {
		super(focDesc, list, uniquePopup);
	}

	public FMenuAction(FocDesc focDesc, FocList list, int viewID, boolean uniquePopup) {
		super(focDesc, list, viewID, uniquePopup);
	}

	public FMenuAction(FocDesc focDesc, boolean uniquePopup) {
		super(focDesc, uniquePopup);
	}

	@Override
	public FPanel generatePanel() {
		return focDesc.callNewBrowsePanel(list, viewID);
	}
}
