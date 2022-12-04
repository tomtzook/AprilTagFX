package com.flash3388.apriltagfx.gui.panes;

import com.flash3388.apriltagfx.vision.runner.Result;
import com.flash3388.apriltagfx.vision.runner.TagInfo;
import com.jmath.vectors.Vector3;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class OutputView extends HBox {

    private final TableView<TagInfo> mResultTable;

    public OutputView() {
        mResultTable = new TableView<>();
        mResultTable.setMinWidth(500);
        getChildren().add(mResultTable);

        TableColumn<TagInfo, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<TagInfo, Double> distanceColumn = new TableColumn<>("Distance [M]");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distanceM"));
        distanceColumn.setMinWidth(100);
        TableColumn<TagInfo, Double> azimuthColumn = new TableColumn<>("Azimuth [Deg]");
        azimuthColumn.setCellValueFactory(new PropertyValueFactory<>("azimuthDegrees"));
        azimuthColumn.setMinWidth(100);
        TableColumn<TagInfo, Double> inclinationColumn = new TableColumn<>("Inclination [Deg]");
        inclinationColumn.setCellValueFactory(new PropertyValueFactory<>("inclinationDegrees"));
        inclinationColumn.setMinWidth(100);
        TableColumn<TagInfo, Vector3> centerPointColumn = new TableColumn<>("Center");
        centerPointColumn.setCellValueFactory(new PropertyValueFactory<>("objectCenter"));
        centerPointColumn.setCellFactory((p)-> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Vector3 item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(String.format("%.2f, %.2f, %.2f", item.x(), item.y(), item.z()));
                    }
                }
            };
        });
        centerPointColumn.setMinWidth(150);

        setPadding(new Insets(5));
        setMaxHeight(200);
        //noinspection unchecked
        mResultTable.getColumns().addAll(idColumn, distanceColumn, azimuthColumn, inclinationColumn, centerPointColumn);
    }

    public void setOutput(Result result) {
        mResultTable.getItems().clear();

        if (result == null) {
            return;
        }

        mResultTable.getItems().addAll(result.getValues());
    }

    public void clear() {
        mResultTable.getItems().clear();
    }
}
