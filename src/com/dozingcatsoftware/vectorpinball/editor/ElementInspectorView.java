package com.dozingcatsoftware.vectorpinball.editor;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.dozingcatsoftware.vectorpinball.editor.elements.EditableFieldElement;
import com.dozingcatsoftware.vectorpinball.editor.inspector.ElementInspector;

public class ElementInspectorView extends VBox {

    ElementSelection elementSelection;
    Label selectionLabel;

    Object inspectedObject;
    Pane inspectorPane;

    Runnable changeCallback;

    public ElementInspectorView() {
        selectionLabel = new Label("");
        this.getChildren().add(selectionLabel);
        update();
    }

    public void setElementSelection(ElementSelection selection) {
        elementSelection = selection;
    }

    public void setChangeCallback(Runnable callback) {
        changeCallback = callback;
    }

    public void update() {
        if (elementSelection != null && elementSelection.hasSelection()) {
            EditableFieldElement elem = elementSelection.getSelectedElements().iterator().next();
            if (inspectedObject != elem) {
                String className = (String)elem.getProperty(EditableFieldElement.CLASS_PROPERTY);
                selectionLabel.setText(className);
                // Create inspector view...
                this.getChildren().remove(inspectorPane);
                this.getChildren().add(inspectorPane = new Pane());

                ElementInspector inspector = null;
                try {
                    String inspectorClass = "com.dozingcatsoftware.vectorpinball.editor.inspector." +
                            className + "Inspector";
                    inspector = (ElementInspector)Class.forName(inspectorClass).newInstance();
                    inspector.initialize(inspectorPane, elem, changeCallback);
                }
                catch(InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else {
            selectionLabel.setText("No selection");
            this.getChildren().remove(inspectorPane);
            inspectorPane = null;
        }
    }
}