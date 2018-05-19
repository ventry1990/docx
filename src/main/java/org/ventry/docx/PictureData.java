package org.ventry.docx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * file: org.ventry.docx.PictureData
 * author: ventry
 * create: 18/5/19 15:34
 * description:
 */

class PictureData {
    /**
     * 文件名
     */
    private String name;
    /**
     * 二进制数据
     */
    private byte[] data;
    /**
     * 垂直位置（单位：pt）
     */
    private double top;
    /**
     * 水平位置（单位：pt）
     */
    private double left;
    /**
     * 宽度（单位：pt）
     */
    private double width;
    /**
     * 长度（单位：pt）
     */
    private double height;

    PictureData() {
        this("", new byte[0]);
    }

    PictureData(String name, byte[] data) {
        this.name = name.toLowerCase();
        this.data = data;
    }

    boolean isWmf() {
        return name.contains("wmf");
    }

    boolean isEmf() {
        return name.contains("emf");
    }

    void sizeTo(double w, double h) {
        sizeTo(w, h, LengthUnit.POINTS);
    }

    void sizeTo(double w, double h, LengthUnit unit) {
        width = unit.toPoints(w);
        height = unit.toPoints(h);
    }

    boolean isShaped() {
        return width > 0D || height > 0D;
    }

    void moveTo(double t, double l) {
        moveTo(t, l, LengthUnit.POINTS);
    }

    void moveTo(double t, double l, LengthUnit unit) {
        top = unit.toPoints(t);
        left = unit.toPoints(l);
    }

    boolean isAnchored() {
        return top > 0D || left > 0D;
    }

    String getName() {
        return name;
    }

    byte[] getData() {
        return data;
    }

    double getTop() {
        return top;
    }

    double getLeft() {
        return left;
    }

    double getWidth() {
        return width;
    }

    double getHeight() {
        return height;
    }

    InputStream getStream() {
        return new ByteArrayInputStream(data);
    }
}
