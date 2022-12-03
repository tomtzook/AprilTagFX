package com.flash3388.apriltagfx.gui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LabeledDoubleSlider extends VBox {

    private final Slider mSlider;

    public LabeledDoubleSlider(String name, double min, double max) {
        mSlider = new Slider();
        mSlider.setMin(min);
        mSlider.setMax(max);
        mSlider.setBlockIncrement(1);
        mSlider.setMajorTickUnit(1);
        mSlider.setMinorTickCount(0);
        mSlider.setPrefWidth(200);

        NumericField valueField = new NumericField(Double.class);
        valueField.setPrefWidth(70);
        valueField.setText("0.0");

        Label valueLbl = new Label(String.format("%.3f", getValue()));
        HBox labels = new HBox();
        labels.setSpacing(2);
        labels.getChildren().addAll(
                new Label(name),
                valueLbl
        );

        mSlider.valueProperty().addListener((obs, o, n)-> {
            valueLbl.setText(String.format("%.3f", n.doubleValue()));
        });
        valueField.valueProperty().bindBidirectional(mSlider.valueProperty());

        HBox valuesPane = new HBox();
        valuesPane.setSpacing(5);
        valuesPane.setAlignment(Pos.CENTER_LEFT);
        valuesPane.getChildren().addAll(mSlider, valueField);

        setSpacing(2);
        getChildren().addAll(labels, valuesPane);
    }

    public double getValue() {
        return mSlider.getValue();
    }

    public DoubleProperty valueProperty() {
        return mSlider.valueProperty();
    }
}
