package org.ventry.docx.picture;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * file: org.ventry.docx.picture.PictureData
 * author: ventry
 * create: 18/5/19 15:34
 * description:
 */

class PictureData {
    /**
     * File Name
     */
    private String name;
    /**
     * Content
     */
    private byte[] data;
    /**
     * Vertical Position (Unit：pt)
     */
    private double top;
    /**
     * Horizontal Position (Unit：pt)
     */
    private double left;
    /**
     * (Unit：pt)
     */
    private double width;
    /**
     * (Unit：pt)
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