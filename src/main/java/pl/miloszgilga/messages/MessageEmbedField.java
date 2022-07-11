package pl.miloszgilga.messages;


public class MessageEmbedField {

    private final String name;
    private final String description;
    private final boolean ifInline;

    public MessageEmbedField(String name, String description, boolean ifInline) {
        this.name = name;
        this.description = description;
        this.ifInline = ifInline;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIfInline() {
        return ifInline;
    }
}