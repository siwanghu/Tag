package com.ws.untils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.ws.dao.DirDao;
import com.ws.dao.VoiceDao;
import com.ws.entity.Dir;
import com.ws.entity.Voice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class OSSUrl {
    private static final String ENDPOINT="oss-cn-beijing.aliyuncs.com";
    private static final String ACCESSKEYID = "LTAI3D5FNNXlQ5gW";
    private static final String ACCESSKEYSECRET = "helkPac04CjMCwGnRPHfs2JYlgTB3P";
    private static final String BUCKETNAME = "ws-record-backup";
    private static final String BUCKETSPLIT="ws-record-split";

    private static final String RAWWAVDIR="C:\\Users\\ylchen\\Desktop\\vad\\raw_wav";
    private static final String MONODIR="C:\\Users\\ylchen\\Desktop\\vad\\mono";
    private static final String SPLITDIR="C:\\Users\\ylchen\\Desktop\\vad\\split";

//    private static final String RAWWAVDIR="/root/vad/raw_wav";
//    private static final String MONODIR="/root/vad/mono";
//    private static final String SPLITDIR="/root/vad/split";

    private static final String VADURL="http://127.0.0.1:5555/vad";
    private static final String CLEARURL="http://127.0.0.1:5555/clear";

    @Autowired
    private  Http http;

    @Autowired
    private VoiceDao voiceDao;

    @Autowired
    private DirDao dirDao;

    public  String getUrl(String objectName){
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        URL url = ossClient.generatePresignedUrl(BUCKETSPLIT, objectName, expiration);
        return url.toString();
    }

    public  List<String> getDir(){
        List<String> uses=new ArrayList<String>();
        List<Dir> beans=(List<Dir>) dirDao.findAll();
        for(Dir bean :beans){
            uses.add(bean.getDir());
        }
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        List<String> dirs= new ArrayList<String>();
        final int maxKeys = 200;
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKETNAME).withMarker(nextMarker).withMaxKeys(maxKeys));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                String[] split=s.getKey().split("/");
                if(!dirs.contains(split[0])&&!uses.contains(split[0])){
                    dirs.add(split[0]);
                }
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        return dirs;
    }

    public List<String> getSplitDir(){
        List<String> uses=new ArrayList<String>();
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        List<String> dirs= new ArrayList<String>();
        final int maxKeys = 200;
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKETSPLIT).withMarker(nextMarker).withMaxKeys(maxKeys));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                String[] split=s.getKey().split("/");
                if(!dirs.contains(split[0])&&!uses.contains(split[0])){
                    dirs.add(split[0]);
                }
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        return dirs;
    }

    public  List<String> getVoiceList(String dir){
        int maxKeys=200;
        List<String> list=new ArrayList<String>();
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKETNAME).withMarker(nextMarker).withMaxKeys(maxKeys).withPrefix(dir));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
                String[] temp=s.getKey().split("/");
                list.add(temp[1]);
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        ossClient.shutdown();
        return list;
    }

    public List<String> getSplitVoiceList(String dir){
        int maxKeys=200;
        List<String> list=new ArrayList<String>();
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
        String nextMarker = null;
        ObjectListing objectListing;
        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKETSPLIT).withMarker(nextMarker).withMaxKeys(maxKeys).withPrefix(dir));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
                String[] temp=s.getKey().split("/");
                list.add(temp[1]);
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());
        ossClient.shutdown();
        return list;
    }

    public  boolean downloadFile(String dir){
        try {
            int maxKeys=200;
            List<String> list=new ArrayList<String>();
            OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
            String nextMarker = null;
            ObjectListing objectListing;
            do {
                objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKETNAME).withMarker(nextMarker).withMaxKeys(maxKeys).withPrefix(dir));
                List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    list.add(s.getKey().split("/")[1]);
                    System.out.println("下载文件:"+s.getKey());
                    String[] temp = s.getKey().split("/");
                    ossClient.getObject(new GetObjectRequest(BUCKETNAME, s.getKey()), new File(RAWWAVDIR + File.separatorChar + temp[1]));
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
            ossClient.shutdown();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public  boolean splitWav(){
        try {
            System.out.println("发送切分命令");
            http.get(VADURL);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public  boolean uploadFile(){
        try {
            OSSClient ossClient = new OSSClient(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET);
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String datetime = sdf.format(dt);
            File path = new File(SPLITDIR);
            File[] array = path.listFiles();
            for (File file : array) {
                String name = file.getName();
                System.out.println("上传文件:"+name);
                ossClient.putObject(BUCKETSPLIT, datetime + "/" + name, new File(SPLITDIR + File.separatorChar + name));
                Voice voice = new Voice();
                voice.setName(name);
                voice.setLabel("");
                voice.setUri(datetime + "/" + name);
                voice.setDatetime(java.sql.Date.valueOf(datetime));
                voice.setCommit("uncheck");
                long size = (file.length() / 2014);
                voice.setSize(new String(size + "KB"));
                voiceDao.save(voice);
            }
            ossClient.shutdown();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public boolean clear(String dir){
        try {
            System.out.println("发送清除命令!");
            http.get(CLEARURL);
        }catch (Exception e){
            return false;
        }
        System.out.println("保存数据库："+dir);
        Dir bean=new Dir();
        bean.setDir(dir);
        dirDao.save(bean);
        return true;
    }
}
