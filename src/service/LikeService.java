package com.example.flownary.service;

import java.util.List;


import com.example.flownary.entity.Like_;

public interface LikeService {

	Like_ getLike(int lid);
	
	Like_ getLikeUid(int uid, int type, int oid);

	List<Like_> getLikeList(int type, int oid);
	
	List<Like_> getLikeListFuid(int fuid);
	
	int getLikeCount(int type, int oid);
	
	void insertLike(Like_ like_);
	
	void remakeLike(int lid);
	
	void removeLike(int lid);
}
