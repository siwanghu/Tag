package com.ws.dao;

import com.ws.entity.Dir;
import com.ws.entity.Voice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DirDao extends CrudRepository<Dir,Integer> {
}
