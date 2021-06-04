package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name= "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "temperature")
    private String temperature;
    @Column(name = "humidity")
    private String humidity;
    @Column(name = "date")
    private String currentDate;
    @Column(name = "time")
    private String currentTime;
    @Column(name = "remark")
    private String remark;

    public void setRecordData(String temperature,String humidity,String currentDate,String currentTime,String remark){
        this.temperature = temperature;
        this.humidity = humidity;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.remark = remark;
    }
}
