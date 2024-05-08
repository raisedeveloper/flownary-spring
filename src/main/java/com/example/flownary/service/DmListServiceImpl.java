package com.example.flownary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.flownary.dao.DmListDao;
import com.example.flownary.entity.DmList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DmListServiceImpl implements DmListService {

	private final DmListDao dDao;

	@Override
	public DmList getDmList(int did) {
		return dDao.getDmList(did);
	}

	@Override
	public List<DmList> getDmListList(int cid) {
		return dDao.getDmListList(cid);
	}

	@Override
	public List<DmList> getDmListListByUid(int uid) {
		return dDao.getDmListListByUid(uid);
	}

	@Override
	public void insertDmList(DmList dmList) {
		dDao.insertDmList(dmList);
	}

	@Override
	public void deleteDmList(int did) {
		dDao.deleteDmList(did);
	}
	
}
