package com.ws.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name="tb_voice")
public class Voice implements Serializable{
    private static final long serialVersionUID = 2300044412175011558L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(nullable = false,name = "id")
    private int id;

    @Column(nullable = false , name = "name")
    private String name;

    @Column(nullable = false , name = "label")
    private String label;

    @Column(nullable = false , name = "datetime")
    private Date datetime;

    @Column(nullable = false , name = "uri")
    private String uri;

    @Column(nullable = false , name = "commit")
    private String commit;

    @Column(nullable = false , name = "size")
    private String size;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getUri() {
        return uri;
    }

    public String getCommit() {
        return commit;
    }

    public String getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Voice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", datetime=" + datetime +
                ", uri='" + uri + '\'' +
                ", commit='" + commit + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
