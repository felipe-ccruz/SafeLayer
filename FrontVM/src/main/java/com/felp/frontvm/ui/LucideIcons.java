package com.felp.frontvm.ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Ícones do lucide.dev convertidos para SVGPath.
 * Cada método retorna um Group com tamanho fixo (size x size) e ícone centralizado.
 */
public final class LucideIcons {

    private LucideIcons() {}

    public static Group plus(double size, Color color) {
        return outline("M5 12 H19 M12 5 V19", size, color, 2.0);
    }

    public static Group play(double size, Color color) {
        return filled("M6 3 L20 12 L6 21 Z", size, color);
    }

    public static Group pause(double size, Color color) {
        return filled("M6 4 H10 V20 H6 Z M14 4 H18 V20 H14 Z", size, color);
    }

    public static Group square(double size, Color color) {
        return outline("M5 5 H19 V19 H5 Z", size, color, 2.0);
    }

    public static Group trash(double size, Color color) {
        return outline(
                "M3 6 H21 " +
                "M8 6 V4 a2 2 0 0 1 2 -2 H14 a2 2 0 0 1 2 2 V6 " +
                "M19 6 V20 a2 2 0 0 1 -2 2 H7 a2 2 0 0 1 -2 -2 V6 " +
                "M10 11 V17 M14 11 V17",
                size, color, 1.8);
    }

    public static Group monitor(double size, Color color) {
        return outline(
                "M4 3 H20 a2 2 0 0 1 2 2 V15 a2 2 0 0 1 -2 2 H4 a2 2 0 0 1 -2 -2 V5 a2 2 0 0 1 2 -2 Z " +
                "M8 21 H16 M12 17 V21",
                size, color, 1.8);
    }

    public static Group x(double size, Color color) {
        return outline("M18 6 L6 18 M6 6 L18 18", size, color, 2.0);
    }

    public static Group check(double size, Color color) {
        return outline("M20 6 L9 17 L4 12", size, color, 2.5);
    }

    public static Group power(double size, Color color) {
        return outline("M12 2 V12 M18.36 6.64 a9 9 0 1 1 -12.73 0", size, color, 2.0);
    }

    private static Group outline(String d, double size, Color color, double strokeWidth) {
        SVGPath p = new SVGPath();
        p.setContent(d);
        p.setStroke(color);
        p.setStrokeWidth(strokeWidth);
        p.setFill(Color.TRANSPARENT);
        p.setStrokeLineCap(StrokeLineCap.ROUND);
        p.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return wrap(p, size);
    }

    private static Group filled(String d, double size, Color color) {
        SVGPath p = new SVGPath();
        p.setContent(d);
        p.setFill(color);
        p.setStroke(null);
        return wrap(p, size);
    }

    private static Group wrap(SVGPath p, double size) {
        double scale = size / 24.0;
        p.setScaleX(scale);
        p.setScaleY(scale);
        return new Group(p);
    }
}
