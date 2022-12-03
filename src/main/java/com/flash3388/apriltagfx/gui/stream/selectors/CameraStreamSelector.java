package com.flash3388.apriltagfx.gui.stream.selectors;

import com.flash3388.apriltagfx.gui.stream.CameraStream;
import com.flash3388.apriltagfx.gui.stream.ImageStream;
import javafx.scene.control.ChoiceDialog;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CameraStreamSelector implements StreamSelector {

    private static final int MAX_DEV_COUNT = 5;

    @Override
    public Optional<ImageStream> selectNew() throws Exception {
        VideoCapture videoCapture = new VideoCapture();
        Optional<Integer> selectedDeviceOptional = selectCamera(videoCapture);
        if (!selectedDeviceOptional.isPresent()) {
            return Optional.empty();
        }

        int selectedDevice = selectedDeviceOptional.get();
        if (!videoCapture.open(selectedDevice)) {
            throw new Exception("Failed to open camera: " + selectedDevice);
        }

        return Optional.of(new CameraStream(videoCapture));
    }

    private Optional<Integer> selectCamera(VideoCapture videoCapture) throws Exception {
        List<Integer> connectedDevices = getAllPossibleCameras(videoCapture);
        if (connectedDevices.isEmpty()) {
            throw new Exception("No connected cameras");
        }


        ChoiceDialog<Integer> cameraChoiceDialog = new ChoiceDialog<>(connectedDevices.get(0), connectedDevices);
        cameraChoiceDialog.setTitle("Camera Select");
        cameraChoiceDialog.setHeaderText("Select Camera Device Index");

        return cameraChoiceDialog.showAndWait();
    }

    private List<Integer> getAllPossibleCameras(VideoCapture videoCapture) {
        List<Integer> devices = new ArrayList<>();
        for (int dev = 0; dev < MAX_DEV_COUNT; dev++) {
            if (videoCapture.open(dev)) {
                videoCapture.release();
                devices.add(dev);
            } else {
                break;
            }
        }

        return devices;
    }
}
