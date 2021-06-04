package com.example.demo.serviceImpl;

import com.example.demo.config.AutowiredUtils;
import com.example.demo.dao.RecordDao;
import com.example.demo.entity.Record;
import com.example.demo.service.RecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceimpl implements RecordService {

    private RecordDao recordDao = AutowiredUtils.getBean(RecordDao.class);

    @Override
    public List<Record> getWateringRecord(String date) {
        return recordDao.findByCurrentDate(date);
    }

    @Override
    public void setRecordData(Record record) {
        recordDao.save(record);
    }

    @Override
    public Record getAirData(String currentDate, String currentTime) {
        return recordDao.findByCurrentDateAndCurrentTime(currentDate,currentTime);
    }
}
