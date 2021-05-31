package pl.mateo.upgradulla.actions;

import java.io.Serializable;

public class KnowledgeBasePiece  implements Serializable {


    public enum Type {
        XML, JAVA
    }

    private String id;
    private String name;
    private Type type;

    private String replacement;
    private String comment;
    // ...

    public class _links {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}