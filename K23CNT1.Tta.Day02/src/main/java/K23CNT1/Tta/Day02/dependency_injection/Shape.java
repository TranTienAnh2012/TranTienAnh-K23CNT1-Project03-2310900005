package K23CNT1.Tta.Day02.dependency_injection;

// Dependency Injection
interface Shape {
    void drawShape();
}

class CircleShape implements Shape {
    @Override
    public void drawShape() {
        System.out.println("CircleShape draw");
    }
}

class RectangleShape implements Shape {
    @Override
    public void drawShape() {
        System.out.println("RectangleShape draw");
    }
}

class DrawShape {
    private Shape shape;

    public DrawShape(Shape shape) {
        this.shape = shape;
    }

    public void drawShape() {
        shape.drawShape();
    }
}

 class Main {
    public static void main(String[] args) {
        DrawShape drawShape = new DrawShape(new CircleShape());
        drawShape.drawShape();

        drawShape = new DrawShape(new RectangleShape());
        drawShape.drawShape();
    }
}