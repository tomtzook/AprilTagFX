package com.flash3388.apriltagfx.io;

import com.flash3388.apriltagfx.vision.CamConfig;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CamConfigIo {

    private static final long MAGIC = 0x12331;

    public static CamConfig load(Path path) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            long magic = dataInputStream.readLong();
            if (magic != MAGIC) {
                throw new IOException("Bad File");
            }

            return load(dataInputStream);
        }
    }

    public static void save(Path path, CamConfig camConfig) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path);
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)){
            dataOutputStream.writeLong(MAGIC);
            save(dataOutputStream, camConfig);
        }
    }

    private static void save(DataOutput output, CamConfig camConfig) throws IOException {
        writeMat(output, camConfig.getIntrinsicMatrix());
        writeMat(output, camConfig.getDistCoefficients());
        writeMatList(output, camConfig.getRVectors());
        writeMatList(output, camConfig.getTVectors());
    }

    private static CamConfig load(DataInput dataInput) throws IOException {
        Mat intrinsicMatrix = readMat(dataInput);
        MatOfDouble distCoefficients = new MatOfDouble();
        Mat dd = readMat(dataInput);
        dd.copyTo(distCoefficients);
        dd.release();

        List<Mat> rVectors = readMatList(dataInput);
        List<Mat> tVectors = readMatList(dataInput);

        return new CamConfig(intrinsicMatrix, distCoefficients, rVectors, tVectors);
    }

    private static List<Mat> readMatList(DataInput input) throws IOException {
        int size = input.readInt();
        List<Mat> mats = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mats.add(readMat(input));
        }

        return mats;
    }

    private static void writeMatList(DataOutput output, List<Mat> list) throws IOException {
        output.writeInt(list.size());
        for (Mat mat : list) {
            writeMat(output, mat);
        }
    }

    private static Mat readMat(DataInput input) throws IOException {
        int rows = input.readInt();
        int cols = input.readInt();
        int type = input.readInt();

        Mat mat = Mat.zeros(rows, cols, type);

        int contentSize = input.readInt();
        byte[] matContent = new byte[contentSize];
        input.readFully(matContent);
        ByteBuffer byteBuffer = ByteBuffer.wrap(matContent);

        switch (type) {
            case CvType.CV_64F: {
                DoubleBuffer buffer = byteBuffer.asDoubleBuffer();
                double[] data = new double[contentSize / 8];
                buffer.get(data);
                mat.put(0, 0, data);
                break;
            }
            default:
                throw new IOException("unsupported type: " + type);
        }

        return mat;
    }

    private static void writeMat(DataOutput output, Mat mat) throws IOException {
        output.writeInt(mat.rows());
        output.writeInt(mat.cols());
        output.writeInt(mat.type());

        switch (mat.type()) {
            case CvType.CV_64F:
            {
                double[] data = new double[(int) mat.total()];
                mat.get(0, 0, data);
                ByteBuffer buffer = ByteBuffer.allocate(data.length * 8);
                buffer.asDoubleBuffer().put(data);

                output.writeInt(data.length * 8);
                output.write(buffer.array());
                break;
            }
            default:
                throw new IOException("unsupported mat type: " + mat.type());
        }
    }
}
