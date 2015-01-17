package com.dozingcatsoftware.vectorpinball.editor.inspector;

import java.util.Arrays;
import java.util.List;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.dozingcatsoftware.vectorpinball.editor.elements.EditableWallElement;

public class WallElementInspector extends ElementInspector<EditableWallElement> {

    List<TextField> endpointTextFields;

    @Override void drawInPane(Pane pane) {
        VBox box = new VBox();
        box.getChildren().add(createHBoxWithLabel("Wall"));
        // Position property has 4 values, 2 each for start and end.
        endpointTextFields = Arrays.asList(
                createEndpointTextField(),
                createEndpointTextField(),
                createEndpointTextField(),
                createEndpointTextField());

        HBox startBox = createHBoxWithLabel("Start");
        startBox.getChildren().addAll(endpointTextFields.get(0), endpointTextFields.get(1));
        box.getChildren().add(startBox);

        HBox endBox = createHBoxWithLabel("End");
        endBox.getChildren().addAll(endpointTextFields.get(2), endpointTextFields.get(3));
        box.getChildren().add(endBox);

        box.getChildren().add(createDecimalStringFieldWithLabel(
                "Kick", EditableWallElement.KICK_PROPERTY));

        box.getChildren().add(createBooleanCheckBoxFieldWithLabel(
                "Retract when hit", EditableWallElement.RETRACT_WHEN_HIT_PROPERTY));

        pane.getChildren().add(box);
    }

    TextField createEndpointTextField() {
        DecimalTextField field = new DecimalTextField();
        field.textProperty().addListener((event) -> updateEndpoints());
        return field;
    }

    void updateEndpoints() {
        if (updatingFromExternalChange) return;
        List<String> endpoints = Arrays.asList(
                endpointTextFields.get(0).getText(),
                endpointTextFields.get(1).getText(),
                endpointTextFields.get(2).getText(),
                endpointTextFields.get(3).getText());
        getElement().setProperty(EditableWallElement.POSITION_PROPERTY, endpoints);
        notifyChanged();
    }

    @Override public void updateCustomControlValues() {
        List<?> endpoints = (List<?>)getElement().getProperty(EditableWallElement.POSITION_PROPERTY);
        for (int i=0; i<4; i++) {
            endpointTextFields.get(i).setText(endpoints.get(i).toString());
        }
    }
}