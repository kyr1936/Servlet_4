package com.yr.board.qna;

import java.io.File;
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
import com.yr.page.SearchMakePage;
import com.yr.page.SearchPager;
import com.yr.page.SearchRow;

import com.yr.upload.UploadDAO;
import com.yr.upload.UploadDTO;
import com.yr.util.DBConnector;

public class QnaService implements Action {
	private QnaDAO qnaDAO;
	private UploadDAO uploadDAO;
	
	public QnaService() {
		qnaDAO = new QnaDAO();
		uploadDAO = new UploadDAO();

	}
	@Override
	public ActionForward list(HttpServletRequest request, HttpServletResponse response) {
		ActionForward actionForward = new ActionForward();
		int curPage=1;
		
		try {
			curPage = Integer.parseInt(request.getParameter("curPage"));
		}catch (Exception e) {
			// TODO: handle exception
		}
		String kind = request.getParameter("kind");
		String search = request.getParameter("search");
		
		SearchMakePage searchMakePage = new SearchMakePage(curPage, kind, search);
		
		//row
		 SearchRow searchRow = searchMakePage.makeRow();
		//page
		 int totalCount=0;
		 Connection con = null;
		try {
			con = DBConnector.getConnect();
			totalCount = qnaDAO.getTotalCount(searchRow, con);
			List<BoardDTO> ar = qnaDAO.selectList(searchRow, con);
			System.out.println(ar.size());
			request.setAttribute("list", ar);
			request.setAttribute("board", "qna");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 SearchPager searchPager = searchMakePage.makePage(totalCount);
		 request.setAttribute("pager", searchPager);
		 
		actionForward.setCheck(true);
		actionForward.setPath("../WEB-INF/views/board/boardList.jsp");
		
		return actionForward;
	}

	@Override
	public ActionForward select(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActionForward insert(HttpServletRequest request, HttpServletResponse response) {
		ActionForward actionForward = new ActionForward();
		boolean check = true;
		String path = "../WEB-INF/views/board/boardWrite.jsp";
		String method=request.getMethod();
		
		if(method.equals("POST")) {
			String saveDirectory = request.getServletContext().getRealPath("upload");
			int maxPostSize = 1024*1024*100;
			Connection con=null;

			try {
				MultipartRequest multipartRequest = new MultipartRequest(request, saveDirectory, maxPostSize,"UTF-8", new DefaultFileRenamePolicy());
				Enumeration<String> e = multipartRequest.getFileNames();
				ArrayList<UploadDTO> ar = new ArrayList<UploadDTO>();
				while(e.hasMoreElements()) {
					String s = e.nextElement();
					String fname=multipartRequest.getFilesystemName(s);
					String oname = multipartRequest.getOriginalFileName(s);
					UploadDTO uploadDTO = new UploadDTO();
					uploadDTO.setFname(fname);
					uploadDTO.setOname(oname);
					ar.add(uploadDTO);
				}
				
				QnaDTO qnaDTO = new QnaDTO();
				qnaDTO.setTitle(multipartRequest.getParameter("title"));
				qnaDTO.setWriter(multipartRequest.getParameter("writer"));
				qnaDTO.setContents(multipartRequest.getParameter("contents"));
				con = DBConnector.getConnect();
				
				
				//1. 시퀀스번호 가져오기
				int num = qnaDAO.getNum();
				qnaDTO.setNum(num);
				
				con.setAutoCommit(false);
				//2. qna insert
				num = qnaDAO.insert(qnaDTO, con);
				
				//3. upload insert
				for(UploadDTO uploadDTO : ar) {
					uploadDTO.setNum(qnaDTO.getNum());
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
			actionForward.setPath("./qnaList");
			
		}
		request.setAttribute("board", "qna");
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
	public ActionForward reply(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
}
