package org.ventry.docx.picture;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * file: org.ventry.docx.picture.EmfConverter
 * author: ventry
 * create: 18/5/19 15:32
 * description:
 */

final class EmfConverter {

    private EmfConverter() {
        // Don't care this.
    }

    static OutputStream convert(InputStream inputStream) throws IOException {
        try (EMFInputStream emfStream = new EMFInputStream(inputStream, EMFInputStream.DEFAULT_VERSION)) {
            EMFRenderer emfRenderer = new EMFRenderer(emfStream);
            Rectangle emfBound = emfStream.readHeader().getBounds();
            final int width = (int) emfBound.getWidth() + (int) emfBound.getX();
            final int height = (int) emfBound.getHeight();
            final BufferedImage origin = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            emfRenderer.paint(origin.createGraphics());

            OutputStream output = new ByteArrayOutputStream();
            ImageIO.write(origin, "png", output);
            return output;
        }
    }
}
