package com.foc.annotations.processors;

import javax.lang.model.element.VariableElement;

public interface IFocDataAttributeProcessor {
    String getGetterSetter(FocDataProcessor focDataProcessor, VariableElement field);
}
