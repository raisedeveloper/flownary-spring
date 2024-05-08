package com.example.flownary.service;

import java.util.List;

import com.example.flownary.entity.Notice;

public interface NoticeService {

	Notice getNotice(int nid);
	
	List<Notice> getNoticeList(int uid, int type);
	
	List<Notice> getNoticeListAll(int uid);
	
	void insertNotice(Notice notice);
	
	void updateNotice(Notice notice);
	
	void removeNotice(Notice notice);
	
	void removeNoticeAll(int uid);
}
