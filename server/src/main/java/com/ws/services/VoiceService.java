package com.ws.services;

import com.ws.dao.VoiceDao;
import com.ws.entity.Voice;
import com.ws.untils.OSSUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoiceService {
    @Autowired
    private VoiceDao voiceDao;

    @Autowired
    private OSSUrl ossUrl;

    public boolean insertVoice(Voice voice){
        voiceDao.save(voice);
        return true;
    }

    public boolean deleteVoiceById(int id){
        voiceDao.deleteById(id);
        return true;
    }

    public Voice findVoiceById(int id){
        Voice voice= voiceDao.findById(id);
        voice.setUri(ossUrl.getUrl(voice.getUri()));
        return voice;
    }

    public String findUriById(int id){
        Voice voice= voiceDao.findById(id);
        voice.setUri(ossUrl.getUrl(voice.getUri()));
        return voice.getUri();
    }

    public Page<Voice> findPage(Date datetime, int pageNumber, int pageSize){
        Sort sort = new Sort(Sort.Direction.ASC,"id");
        Pageable pageable = new PageRequest(pageNumber,pageSize,sort);
        Page<Voice> page= voiceDao.findByDatetime(datetime,pageable);
        for(Voice voice : page){
            voice.setUri(ossUrl.getUrl(voice.getUri()));
        }
        return page;
    }

    public boolean updateVoiceById(int id,String name,String label){
        Voice voice=voiceDao.findById(id);
        voice.setLabel(label);
        voice.setName(name);
        voiceDao.save(voice);
        return true;
    }

    public boolean checkVoiceById(int id,String commit){
        Voice voice=voiceDao.findById(id);
        voice.setCommit(commit);
        voiceDao.save(voice);
        return true;
    }

    public List<String> findVoiceDir(){
        return ossUrl.getDir();
    }

    public List<String> findVoiceByDir(String dir){
        return ossUrl.getVoiceList(dir);
    }

    public List<String> findSplitVoiceDir(){
        return ossUrl.getSplitDir();
    }

    public List<Voice> findSplitVoiceByDir(Date dir){
        List<Voice> voices= voiceDao.findByDatetimeAndCommit(dir,"check");
        for(Voice voice : voices){
            voice.setUri(ossUrl.getUrl(voice.getUri()));
        }
        return voices;
    }

    public boolean downloadFile(String dir){
        return ossUrl.downloadFile(dir);
    }

    public boolean splitWav(){
        ossUrl.splitWav();
        return true;
    }

    public boolean uploadOSS(){
        ossUrl.uploadFile();
        return true;
    }

    public boolean clearDir(String dir){
        ossUrl.clear(dir);
        return true;
    }
}
