package com.ieci.tecdoc.common.invesicres;

import java.io.PrintStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ScrDistribucionActual
implements Serializable {
    private Integer iddist;
    private String dist_actual;

    public ScrDistribucionActual() {
    }

    public ScrDistribucionActual(Integer iddist, String dist_actual) {
        this.iddist = iddist;
        this.dist_actual = dist_actual;
    }

    public Integer getIddist() {
        return this.iddist;
    }

    public void setIddist(Integer iddist) {
        this.iddist = iddist;
    }

    public String getDist_actual() {
        return this.dist_actual;
    }

    public void setDist_actual(String dist_actual) {
        this.dist_actual = dist_actual;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("idDist", (Object)this.getIddist()).toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ScrDistribucionActual)) {
            return false;
        }
        ScrDistribucionActual castOther = (ScrDistribucionActual)other;
        return new EqualsBuilder().append((Object)this.getIddist(), (Object)castOther.getIddist()).isEquals();
    }

    public String toXML() {
        String className = this.getClass().getName();
        className = className.substring(className.lastIndexOf(".") + 1, className.length()).toUpperCase();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append(className);
        buffer.append(">");
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            Field field2 = null;
            String name = null;
            for (Field field2 : fields) {
                name = field2.getName();
                buffer.append("<");
                buffer.append(name.toUpperCase());
                buffer.append(">");
                if (field2.get(this) != null) {
                    buffer.append(field2.get(this));
                }
                buffer.append("</");
                buffer.append(name.toUpperCase());
                buffer.append(">");
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
        buffer.append("</");
        buffer.append(className);
        buffer.append(">");
        return buffer.toString();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.getIddist()).toHashCode();
    }
}
