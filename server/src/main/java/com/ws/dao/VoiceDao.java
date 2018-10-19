package com.ws.dao;

import com.ws.entity.Voice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

public interface VoiceDao extends CrudRepository<Voice,Integer> {
     void deleteById(int id);
     Voice findById(int id);
     Page<Voice> findByDatetime(Date datetime,Pageable pageable);
     List<Voice> findByDatetimeAndCommit(Date datetimre,String commit);
}
