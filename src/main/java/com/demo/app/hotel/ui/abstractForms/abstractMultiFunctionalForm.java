package com.demo.app.hotel.ui.abstractForms;

import com.vaadin.ui.FormLayout;

@SuppressWarnings("serial")
abstract class abstractMultiFunctionalForm extends FormLayout {

	protected abstract void initComponents();

	protected abstract void initComponentsProperties();

	protected abstract void initLayouts();

	protected abstract void setComponentsToLayouts();

	protected abstract void addListeners();

	protected abstract void bindFields();
}