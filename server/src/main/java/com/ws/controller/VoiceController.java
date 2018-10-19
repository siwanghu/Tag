package com.ws.controller;

import com.ws.entity.Voice;
import com.ws.services.LoginServer;
import com.ws.services.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.cache.support.NullValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.Log;

import java.sql.Date;
import java.util.List;

@RestController
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    @Autowired
    private LoginServer loginServer;

    @RequestMapping(value="/voice/add",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean addVoice(Voice voice) {
        return voiceService.insertVoice(voice);
    }

    @RequestMapping(value="/voice/delete",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean deleteVoice(int id) {
        return voiceService.deleteVoiceById(id);
    }

    @RequestMapping(value="/voice/entity",method ={RequestMethod.GET,RequestMethod.POST})
    public Voice findVoice(int id) {
        return voiceService.findVoiceById(id);
    }

    @RequestMapping(value="/voice/findUri",method ={RequestMethod.GET,RequestMethod.POST})
    public String findVoiceUri(int id) {
        return voiceService.findUriById(id);
    }

    @RequestMapping(value="/voice/update",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean updateVoiceById(int id,String name,String label) {
        return voiceService.updateVoiceById(id,name,label);
    }

    @RequestMapping(value="/voice/check",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean checkVoiceById(int id,String commit) {
        return voiceService.checkVoiceById(id,commit);
    }

    @RequestMapping(value="/voice/page",method ={RequestMethod.GET,RequestMethod.POST})
    public Page<Voice> getpage(Date datetime, int page, int size) {
        return voiceService.findPage(datetime,page,size);
    }

    @RequestMapping(value="/voice/dir",method ={RequestMethod.GET,RequestMethod.POST})
    public List<String> getDir() {
        return voiceService.findVoiceDir();
    }

    @RequestMapping(value="/voice/file",method ={RequestMethod.GET,RequestMethod.POST})
    public List<String> getVoiceByDir(String dir) {
        return voiceService.findVoiceByDir(dir);
    }

    @RequestMapping(value="/voice/splitdir",method ={RequestMethod.GET,RequestMethod.POST})
    public List<String> getSplitDir() {
        return voiceService.findSplitVoiceDir();
    }

    @RequestMapping(value="/voice/splitfile",method ={RequestMethod.GET,RequestMethod.POST})
    public List<Voice> getSplitVoiceByDir(Date splitdir) {
        return voiceService.findSplitVoiceByDir(splitdir);
    }

    @RequestMapping(value="/voice/download",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean getFileByOSS(String dir) {
        return voiceService.downloadFile(dir);
    }

    @RequestMapping(value="/voice/split",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean putFileSplit(String dir) {
        return voiceService.splitWav();
    }

    @RequestMapping(value="/voice/upload",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean putFileOSS(String dir) {
        return voiceService.uploadOSS();
    }

    @RequestMapping(value="/voice/clear",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean clearFileDir(String dir) {
        return voiceService.clearDir(dir);
    }

    @RequestMapping(value="/voice/login",method ={RequestMethod.GET,RequestMethod.POST})
    public boolean loginSystem(String username,String password) {
        return loginServer.loginServer(username,password);
    }
}
