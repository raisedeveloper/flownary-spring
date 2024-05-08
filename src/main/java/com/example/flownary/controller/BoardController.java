package com.example.flownary.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flownary.entity.Board;
import com.example.flownary.entity.Like_;
import com.example.flownary.entity.Re_Reply;
import com.example.flownary.entity.Reply;
import com.example.flownary.service.BoardService;
import com.example.flownary.service.LikeService;
import com.example.flownary.service.Re_ReplyService;
import com.example.flownary.service.ReplyService;
import com.example.flownary.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class BoardController {
	private final UserService uSvc;
	private final BoardService bSvc;
	private final LikeService lSvc;
	private final ReplyService rSvc;
	private final Re_ReplyService ReReSvc;
	
	@GetMapping("/list")
	//리스트 페이지로 무한스크롤 구현하려합니다
	public JSONArray boardList(@RequestParam(name="c", defaultValue="1", required=false) int count,
			@RequestParam(name="f", defaultValue="title", required=false) String field,
			@RequestParam(name="f2", defaultValue="", required=false) String field2,
			@RequestParam(name="f3", defaultValue="", required=false) String field3,
			@RequestParam(name="q", defaultValue="", required=false) String query,
			@RequestParam(defaultValue="1", required=false) int type) {
		
		List<Board> list = new ArrayList<>();
		
		switch(type) {
		case 1:
			list = bSvc.getBoardList(count, field, query);			
			break;
		case 2:
			List<String> fieldList = new ArrayList<>();
			fieldList.add(field);
			fieldList.add(field2);
			list = bSvc.getBoardListSearch(count, fieldList, query);
			break;
		case 3: 
			List<String> fieldList1 = new ArrayList<>();
			fieldList1.add(field);
			fieldList1.add(field2);
			fieldList1.add(field3);
			list = bSvc.getBoardListSearch(count, fieldList1, query);
			break;
		default:
			System.out.println("error!");
			break;
		}
		
		JSONArray jArr = new JSONArray();
		for(Board board:list) {
			HashMap<String, Object> hMap = new HashMap<String, Object>();
			
 			hMap.put("bid", board.getBid());
			hMap.put("uid", board.getUid());
			hMap.put("title", board.getTitle());
			hMap.put("bContents", board.getbContents());
			hMap.put("modTime", board.getModTime());
			hMap.put("viewCount", board.getViewCount());
			hMap.put("likeCount", board.getLikeCount());
			hMap.put("replyCount", board.getReplyCount());
			hMap.put("image", board.getImage());
			hMap.put("shareUrl", board.getShareUrl());
			hMap.put("isDeleted", board.getIsDeleted());
			hMap.put("hashTag", board.getHashTag());
			hMap.put("nickname", board.getNickname());
			JSONObject jBoard = new JSONObject(hMap);
			
			jArr.add(jBoard);
		}
		return jArr;
	}
	
	@GetMapping("/replyList")
	public JSONArray replyList(@RequestParam int bid,
			@RequestParam int offset, @RequestParam int limit) {
		List<Reply> list = rSvc.getReplyList(bid, offset, limit);
		JSONArray jArr = new JSONArray();
		for(Reply reply :list) {
			JSONObject jreply = new JSONObject();
 			jreply.put("rid", reply.getRid());
 			jreply.put("bid", reply.getBid());
 			jreply.put("uid", reply.getUid());
 			jreply.put("rContents", reply.getrContents());
 			jreply.put("modTime", reply.getModTime());
 			jreply.put("likeCount", reply.getLikeCount());
 			jreply.put("nickname", reply.getNickname());
			jArr.add(jreply);
		}
		return jArr;
	}
	
	
	@PostMapping("/insert")
	public int insertForm(@RequestBody Board dto) {
		
		String shareUrl = "";
		boolean t = true;
		
		while (t)
		{
			shareUrl = RandomStringUtils.randomAlphanumeric(10);
			
			if (bSvc.getBoardShareUrl(shareUrl) == 0)
			{
				t = false;
				break;
			}
		}
		
		Board board = new Board(dto.getUid(), dto.getTitle()
				, dto.getbContents(), dto.getImage(), shareUrl
				, dto.getNickname(), dto.getHashTag());
		
		bSvc.insertBoard(board);
		return 0;
	}

	@PostMapping("/update")
	public String boardUpdate(@RequestBody Board dto,
			HttpSession session) {
		int sessUid = (int) session.getAttribute("sessUid");
		Board board = new Board(sessUid, dto.getTitle(), dto.getbContents(), dto.getImage(),
				dto.getShareUrl(),dto.getHashTag());
		bSvc.updateBoard(board);
		return "수정되었습니다";
	}
	
	@GetMapping("/detail")
	public JSONObject BoardDetail(@RequestParam int bid, @RequestParam int uid) {
		// 조회 수
		bSvc.updateViewCount(bid);
		
		//Board 객체를 model처럼 어떻게 보내줘야 할지 모르겠어요 흑흑
		Board board = bSvc.getBoard(bid);
		JSONObject jObj = new JSONObject();
		jObj.put("bid", bid);
		jObj.put("title", board.getTitle());
		jObj.put("bContents", board.getbContents());
		jObj.put("modTime", board.getModTime());
		jObj.put("image", board.getImage());
		jObj.put("hashTag", board.getHashTag());
		jObj.put("nickname", board.getNickname());
		// like_에서 type별로 좋아요 처리해야됌
//		Like_ like = lSvc.
		
		// 좋아요 수
		int likeCount = board.getLikeCount();
		bSvc.updateLikeCount(bid, likeCount);
		
		return jObj;
	}
	@PostMapping("/reply")
	public void reply(@RequestBody Reply dto) {
		System.out.println(dto);
		Reply reply = new Reply(dto.getBid(),dto.getUid(),dto.getrContents(),dto.getNickname());
		rSvc.insertReply(reply);
		
        // 댓글 조회수
		Board board = new Board();
		int replyCount = board.getReplyCount();
		bSvc.updateReplyCount(dto.getBid(), replyCount);
		System.out.println(reply);
	}
	
	@PostMapping("/Re_Reply")
	public String Re_reply(@RequestBody Re_Reply dto) {
		Re_Reply re_Reply = new Re_Reply().builder().rid(dto.getRid()).uid(dto.getUid())
				.rrContents(dto.getRrContents()).nickname(dto.getNickname()).build();
		ReReSvc.insertReReply(re_Reply);
		
		return "대댓글이 입력되었습니다";
	}
	
	@GetMapping("/delete")
	public void delete(int bid) {
		bSvc.deleteBoard(bid);
	}
	
//	@GetMapping("/like")
//	public String like(@RequestBody Like_ dto) {
//		Like_ like = lSvc.getLike(dto.getLid());
//		if(like == null) {
//			lSvc.insertLike(new Like_(dto.getUid(),dto.getType(),1));
//		}
//		return "좋아요";
//	}
		
}
