package com.pls.shipment.service.pdf;

import com.itextpdf.text.PageSize;

/**
 * Enumeration with printable template properties.
 * 
 * @author Dmitry Nikolaenko
 *
 */
public enum Printable {
    DEFAULT_TEMPLATE(1, 14, 16, new Logo(180, 65)) {
        @Override
        public float getCellHeight() {
            return 500;
        }
    },
    TEMPLATE1(1, 12, 14, new Logo(150, 65)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / 2 - 20;
        }
    },
    TEMPLATE2(2, 10, 11, new Logo(150, 65)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / 2 - 40;
        }
    },
    TEMPLATE3(2, 10, 11, new Logo(150, 65)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / 2 - 40;
        }
    },
    TEMPLATE4(4, 7, 8, new Logo(100, 55)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getWidth() / (getCells() / 2) - 40;
        }
    },
    TEMPLATE5(6, 6, 7, new Logo(70, 45)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / (getCells() / 2) - 25;
        }
    },
    TEMPLATE6(8, 5, 6, new Logo(80, 40)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / (getCells() / 2) - 20;
        }
    },
    TEMPLATE7(10, 4, 5, new Logo(50, 35)) {
        @Override
        public float getCellHeight() {
            return PageSize.A4.getHeight() / (getCells() / 2) - 15;
        }
    };

    private int cells;
    private float textSize;
    private float titleSize;
    private Logo logo;

    Printable(int cells, int textSize, int titleSize, Logo logo) {
        this.cells = cells;
        this.textSize = textSize;
        this.titleSize = titleSize;
        this.logo = logo;
    }

    /**
     * Method return height for cell of the table.
     * 
     * @return height for cell
     */
    public abstract float getCellHeight();

    /**
     * Method return properties for selected print type.
     * 
     * @param index is position of Printable enum.
     * @return enum value with properties
     */
    public static Printable getPrintable(int index) {
        for (Printable print : Printable.values()) {
            if (print.ordinal() == index) {
                return print;
            }
        }
        return DEFAULT_TEMPLATE;
    }

    public int getCells() {
        return cells;
    }

    public float getTextSize() {
        return textSize;
    }

    public float getTitleSize() {
        return titleSize;
    }

    public Logo getLogo() {
        return logo;
    }

    /**
     * Class that contains maximum size for company logo.
     */
    public static class Logo {
        private float width;
        private float height;

        Logo(float width, float height) {
            this.width = width;
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }
    }
}
