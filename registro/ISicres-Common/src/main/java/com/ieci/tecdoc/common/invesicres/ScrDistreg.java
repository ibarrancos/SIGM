package com.ieci.tecdoc.common.invesicres;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ScrDistreg
implements Serializable {
    private Integer id;
    private int idArch;
    private int idFdr;
    private Date distDate;
    private int typeOrig;
    private int idOrig;
    private int typeDest;
    private int idDest;
    private int state;
    private Date stateDate;
    private String message;
    private Integer iddistfather;

    public ScrDistreg() {
    }

    public ScrDistreg(Integer id, int idArch, int idFdr, Date distDate, int typeOrig, int idOrig, int typeDest, int idDest, int state, Date stateDate) {
        this.id = id;
        this.idArch = idArch;
        this.idFdr = idFdr;
        this.distDate = distDate;
        this.typeOrig = typeOrig;
        this.idOrig = idOrig;
        this.typeDest = typeDest;
        this.idDest = idDest;
        this.state = state;
        this.stateDate = stateDate;
    }

    public ScrDistreg(Integer id, int idArch, int idFdr, Date distDate, int typeOrig, int idOrig, int typeDest, int idDest, int state, Date stateDate, String message, Integer iddistfather) {
        this.id = id;
        this.idArch = idArch;
        this.idFdr = idFdr;
        this.distDate = distDate;
        this.typeOrig = typeOrig;
        this.idOrig = idOrig;
        this.typeDest = typeDest;
        this.idDest = idDest;
        this.state = state;
        this.stateDate = stateDate;
        this.message = message;
        this.iddistfather = iddistfather;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdArch() {
        return this.idArch;
    }

    public void setIdArch(int idArch) {
        this.idArch = idArch;
    }

    public int getIdFdr() {
        return this.idFdr;
    }

    public void setIdFdr(int idFdr) {
        this.idFdr = idFdr;
    }

    public Date getDistDate() {
        return this.distDate;
    }

    public void setDistDate(Date distDate) {
        this.distDate = distDate;
    }

    public int getTypeOrig() {
        return this.typeOrig;
    }

    public void setTypeOrig(int typeOrig) {
        this.typeOrig = typeOrig;
    }

    public int getIdOrig() {
        return this.idOrig;
    }

    public void setIdOrig(int idOrig) {
        this.idOrig = idOrig;
    }

    public int getTypeDest() {
        return this.typeDest;
    }

    public void setTypeDest(int typeDest) {
        this.typeDest = typeDest;
    }

    public int getIdDest() {
        return this.idDest;
    }

    public void setIdDest(int idDest) {
        this.idDest = idDest;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getStateDate() {
        return this.stateDate;
    }

    public void setStateDate(Date stateDate) {
        this.stateDate = stateDate;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getIddistfather() {
        return this.iddistfather;
    }

    public void setIddistfather(Integer iddistfather) {
        this.iddistfather = iddistfather;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).toString();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ScrDistreg)) {
            return false;
        }
        ScrDistreg castOther = (ScrDistreg)other;
        return new EqualsBuilder().append((Object)this.getId(), (Object)castOther.getId()).isEquals();
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
        return new HashCodeBuilder().append((Object)this.getId()).toHashCode();
    }
}
