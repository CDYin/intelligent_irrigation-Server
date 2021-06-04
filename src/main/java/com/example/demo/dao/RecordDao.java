package com.example.demo.dao;

import com.example.demo.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordDao extends JpaRepository<Record,Integer> {
    List<Record> findByCurrentDate(String date);

    Record findByCurrentDateAndCurrentTime(String currentDate,String currentTime);
}
