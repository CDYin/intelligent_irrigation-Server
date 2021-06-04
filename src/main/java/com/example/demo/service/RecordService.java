package com.example.demo.service;

import com.example.demo.entity.Record;

import java.util.List;

public interface RecordService {
    List<Record> getWateringRecord(String date);

    void setRecordData(Record record);

    Record getAirData(String currentDate,String currentTime);
}
