package com.yr.board.notice;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.yr.action.Action;
import com.yr.action.ActionForward;
import com.yr.board.BoardDTO;
import com.yr.board.notice.NoticeDTO;
import com.yr.page.SearchMakePage;
import com.yr.page.SearchPager;
import com.yr.page.SearchRow;
import com.yr.upload.UploadDAO;
import com.yr.upload.UploadDTO;
import com.yr.util.DBConnector;

public class NoticeService implements Action {
	private NoticeDAO noticeDAO;
	private UploadDAO uploadDAO;
	
	public NoticeService() {
		noticeDAO = new NoticeDAO();
		uploadDAO = new UploadDAO();
		// TODO Auto-generated constructor stub
	}

	@Override
	public ActionForward list(HttpServletRequest request, HttpServletResponse response) {
		ActionForward actionForward = new ActionForward();
		System.out.println("service");
		int curPage=1;
		try {
		curPage=Integer.parseInt(request.getParameter("curPage"));
		} catch (Exception e) {
		
		}
		String kind = request.getParameter("kind");
		String search = request.getParameter("search");
		
		SearchMakePage s = new SearchMakePage(curPage, kind, search);
		
		//1. row
		SearchRow searchRow = s.makeRow();
		List<BoardDTO> ar = null;
		Connection con = null;
		try {
			con = DBConnector.getConnect();
			
			ar = noticeDAO.selectList(searchRow, con);
			
		//2. page
			int totalCount = noticeDAO.getTotalCount(searchRow, con);
			SearchPager searchPager = s.makePage(totalCount);
			
			request.setAttribute("pager", searchPager);
			request.setAttribute("list", ar);
			request.setAttribute("board", "notice");
			actionForward.setCheck(true);
			actionForward.setPath("../WEB-INF/views/board/boardList.jsp");
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("message", "Server Error");
			request.setAttribute("path", "../index.do");
			actionForward.setCheck(true);
			actionForward.setPath("../WEB-INF/views/common/result.jsp");
			
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return actionForward;
	}

	@Override
	public ActionForward select(HttpServletRequest request, HttpServletResponse response) {
		ActionForward actionForward = new ActionForward();
		return actionForward;
	
	}

	@Override
	public ActionForward insert(HttpServletRequest request, HttpServletResponse response) {
		ActionForward actionForward = new ActionForward();
		
		String method = request.getMethod(); //get, post
		boolean check = true;
		String path = "../WEB-INF/views/board/boardWrite.jsp";
		
		
		if(method.equals("POST")) {
			//1. request를 하나로 합치기
			//파일을 저장할 디스크 경로(C: D:)
			String saveDirectory =request.getServletContext().getRealPath("upload");
		
			File file = new File(saveDirectory);
			
			if(!file.exists()) {
				file.mkdirs();
			}
			
			
			
			int maxPostSize=1024*1024*10;	// byte단위
			Connection con=null;
			String encoding = "UTF-8";
			try {
				MultipartRequest multi = new MultipartRequest(request, saveDirectory, maxPostSize, encoding, new DefaultFileRenamePolicy());
				Enumeration<String> e = multi.getFileNames();
				ArrayList<UploadDTO> ar = new ArrayList<UploadDTO>();
				while(e.hasMoreElements()) {
					String s = e.nextElement();
					String fname = multi.getFilesystemName(s);
					String oname = multi.getOriginalFileName(s);
					UploadDTO uploadDTO = new UploadDTO();
					uploadDTO.setFname(fname);
					uploadDTO.setOname(oname);
					ar.add(uploadDTO);
				}

			NoticeDTO noticeDTO = new NoticeDTO();
			noticeDTO.setTitle(multi.getParameter("title"));
			noticeDTO.setWriter(multi.getParameter("writer"));
			noticeDTO.setContents(multi.getParameter("contents"));
			
			con = DBConnector.getConnect();
			
			int num = noticeDAO.getNum();
			noticeDTO.setNum(num);
			
			con.setAutoCommit(false);
			
			num = noticeDAO.insert(noticeDTO, con);
			
			for(UploadDTO uploadDTO : ar) {
				uploadDTO.setNum(noticeDTO.getNum());
				num = uploadDAO.insert(uploadDTO, con);
				if(num<1) {
					throw new Exception();
				}
				
			}
			con.commit();
			
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				con.setAutoCommit(true);
				con.close();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			actionForward.setCheck(false);
			actionForward.setPath("./boardList");
			
		}
		request.setAttribute("board", "notice");
		actionForward.setCheck(check);
		actionForward.setPath(path);
				
		return actionForward;
	}

	@Override
	public ActionForward update(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionForward delete(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}
