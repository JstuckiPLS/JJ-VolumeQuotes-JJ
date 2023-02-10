package com.pls.shipment.service.pdf;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * Listener to generate array of strings from pdf.
 * @author Sergii Belodon
 */
public class StringArrayRenderListener implements RenderListener {
    private List<String> textBlocks = new ArrayList<String>(100);
    /**
     * Event handler for beginning of text block.
     * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
     */
    public void beginTextBlock() {
        // do nothing
    }

    /**
     * Event handler for ending of text block.
     * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
     */
    public void endTextBlock() {
        // do nothing
    }

    /**
     * Event handler for image.
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(
     *     com.itextpdf.text.pdf.parser.ImageRenderInfo)
     *     @param renderInfo - render info.
     */
    public void renderImage(ImageRenderInfo renderInfo) {
        // do nothing
    }

    /**
     * Event handler for text.
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(
     *     com.itextpdf.text.pdf.parser.TextRenderInfo)
     *     @param renderInfo - text render info.
     */
    public void renderText(TextRenderInfo renderInfo) {
        textBlocks.add(renderInfo.getText());
    }

    /**
     * Returns content.
     * @return generated array of strings
     */
    public List<String> getTextBlocks() {
        return textBlocks;
    }
}
