package org.ventry.docx.picture;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlip;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosH;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTPosV;
import org.ventry.docx.XmlNamespace;

import java.util.List;

/**
 * file: org.ventry.docx.picture.DrawingReader
 * author: ventry
 * create: 18/5/19 15:38
 * description:
 */

public class DrawingReader extends PictureReader {

    public DrawingReader(PictureProcessor processor) {
        super(processor);
    }

    @Override
    public boolean match(XmlObject object) {
        return object instanceof CTDrawing;
    }

    @Override
    protected PictureData readPictureStream(XWPFDocument document, XmlObject object) {
        CTDrawing drawing = (CTDrawing) object;
        PictureData pictureData = new PictureData();

        XmlCursor cursor = drawing.newCursor();
        cursor.selectPath(XmlNamespace.A.text() + ".//a:blip");
        if (cursor.toNextSelection()) {
            CTBlip blip = (CTBlip) cursor.getObject();
            String relationId = blip.getEmbed();
            XWPFPictureData picture = (XWPFPictureData) document.getRelationById(relationId);
            pictureData = new PictureData(picture.getFileName(), picture.getData());
            sizeTo(drawing, pictureData);
            tryMoveTo(drawing, pictureData);
        }
        cursor.dispose();

        return pictureData;
    }

    private void sizeTo(CTDrawing drawing, PictureData picture) {
        XmlCursor cursor = drawing.newCursor();
        cursor.selectPath(XmlNamespace.WP.text() + ".//wp:extent");
        if (cursor.toNextSelection()) {
            CTPositiveSize2D ext = (CTPositiveSize2D) cursor.getObject();
            picture.sizeTo(ext.getCx(), ext.getCy(), LengthUnit.EMUS);
        }
        cursor.dispose();
    }

    private void tryMoveTo(CTDrawing drawing, PictureData picture) {
        List<CTAnchor> anchors = drawing.getAnchorList();
        for (CTAnchor anchor : anchors) {
            boolean simplePos = anchor.getSimplePos2();
            XmlCursor cursor = anchor.newCursor();
            long left = 0;
            long top = 0;
            if (simplePos) {
                cursor.selectPath(XmlNamespace.WP.text() + "./wp:simplePos");
                if (cursor.toNextSelection()) {
                    CTPoint2D point = (CTPoint2D) cursor.getObject();
                    left = point.getX();
                    top = point.getY();
                    picture.moveTo(top, left, LengthUnit.EMUS);
                }
            } else {
                cursor.selectPath(XmlNamespace.WP.text() + "./wp:positionH");
                if (cursor.toNextSelection()) {
                    left = ((CTPosH) cursor.getObject()).getPosOffset();
                }

                cursor.toParent();
                cursor.selectPath(XmlNamespace.WP.text() + "./wp:positionV");
                if (cursor.toNextSelection()) {
                    top = ((CTPosV) cursor.getObject()).getPosOffset();
                }

                picture.moveTo(top, left, LengthUnit.EMUS);
            }
            cursor.dispose();
        }
    }
}
