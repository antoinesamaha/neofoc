package com.foc.db;

import com.foc.desc.FocObject;

public interface IDBReloader {

	public void reloadTable(FocObject obj, int action);
}
