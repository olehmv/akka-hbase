package hbase.entity;
import javax.imageio.ImageIO;
import com.flipkart.hbaseobjectmapper.HBColumn;
import com.flipkart.hbaseobjectmapper.HBRecord;
import com.flipkart.hbaseobjectmapper.HBRowKey;
import com.flipkart.hbaseobjectmapper.HBTable;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

@HBTable("car")
public class Car implements HBRecord {
    @HBRowKey
    private String brand;
    @HBColumn(family = "info",column = "model")
    private String model;
    @HBColumn(family = "info", column="type")
    private String type;
    @HBColumn(family = "image",column="foto")
    private Map<String,byte[]> fotos;

    public Car(){
    }
    public Car(String brand, String model){
        this.brand =brand;
        this.model =model;
    }

    public Map<String, byte[]> getFotos() {
        return fotos;
    }

    public void setFotos(Map<String, byte[]> fotos) {
        this.fotos = fotos;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String composeRowKey() {
        return brand;
    }

    @Override
    public void parseRowKey(String rowKey) {
            this.brand =rowKey;
    }
}
