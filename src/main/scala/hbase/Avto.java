package hbase;

import com.flipkart.hbaseobjectmapper.HBColumn;
import com.flipkart.hbaseobjectmapper.HBTable;
import com.flipkart.hbaseobjectmapper.*;
import com.flipkart.hbaseobjectmapper.codec.*;

@HBTable("avto")
public class Avto {
    @HBRowKey
    private Integer aid;
    @HBColumn(family = "info",column = "type")
    private String name;

    public Avto(){

    }

    public Avto(Integer aid,String name){
        this.aid=aid;
        this.name=name;
    }

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
