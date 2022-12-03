package com.flash3388.apriltagfx.gui.controls;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LabeledIntSlider extends VBox {

    private final Slider mSlider;

    public LabeledIntSlider(String name, int min, int max) {
        mSlider = new Slider();
        mSlider.setMin(min);
        mSlider.setMax(max);
        mSlider.setBlockIncrement(1);
        mSlider.setMajorTickUnit(1);
        mSlider.setMinorTickCount(0);

        Label valueLbl = new Label(String.valueOf(getValue()));
        HBox labels = new HBox();
        labels.setSpacing(2);
        labels.getChildren().addAll(
                new Label(name),
                valueLbl
        );

        mSlider.valueProperty().addListener((obs, o, n)-> {
            mSlider.setValue(n.intValue());
            valueLbl.setText(String.valueOf(n.intValue()));
        });

        setSpacing(2);
        getChildren().addAll(labels, mSlider);
    }

    public int getValue() {
        return (int) mSlider.getValue();
    }
}
