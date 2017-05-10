package com.fct.message.data.repository;

import com.fct.message.data.entity.SMSRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/6.
 */
public interface SMSRecordRepository extends JpaRepository<SMSRecord, Integer> {

}
