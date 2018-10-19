package com.ws.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tb_dir")
public class Dir implements Serializable {
    private static final long serialVersionUID = 2300044412175011558L;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(nullable = false,name = "id")
    private int id;

    @Column(nullable = false , name = "dir")
    private String dir;

    public int getId() {
        return id;
    }

    public String getDir() {
        return dir;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return "Dir{" +
                "id=" + id +
                ", dir='" + dir + '\'' + '}';
    }
}
