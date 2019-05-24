package com.yr.control;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yr.action.ActionForward;
import com.yr.board.notice.NoticeService;

/**
 * Servlet implementation class NoticeController
 */
@WebServlet("/NoticeController")
public class NoticeController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private NoticeService noticeService;
	

	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NoticeController() {
    	
        super();
        noticeService = new NoticeService();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getPathInfo();
		ActionForward actionForward = null;
		System.out.println("command : " + command);
		if(command.equals("/noticeList")) {
			
			actionForward = noticeService.list(request, response);
			
		} else if(command.equals("/noticeSelect")) {
			actionForward = noticeService.select(request, response);
			
		} else if(command.equals("/noticeWrite")) {
			actionForward=noticeService.insert(request, response);
			
		} else if(command.equals("/noticeUpdate")) {
			actionForward = noticeService.update(request, response);
			
		} else if(command.equals("/noticeDelete")) {
			actionForward = noticeService.delete(request, response);
		}
		
		
		
		
		
		if(actionForward.isCheck()) {
			RequestDispatcher view = request.getRequestDispatcher(actionForward.getPath());
			view.forward(request, response);
		} else {
			response.sendRedirect(actionForward.getPath());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
