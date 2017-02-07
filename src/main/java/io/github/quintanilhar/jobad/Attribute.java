package io.github.quintanilhar.jobad;

final public class Attribute
{
    private String type;
    private String value;

    public Attribute(String type, String value)
    {
        this.type = type;
        this.value = value;
    }

    public String toString()
    {
        return this.type + ": " + value;
    }
}
