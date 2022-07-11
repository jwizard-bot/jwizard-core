package pl.miloszgilga.messages;

public enum EmbedMessageColor {
    GREEN("#19d166"),
    RED("#d11919"),
    ORANGE("#ffa805");

    private final String color;

    EmbedMessageColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}