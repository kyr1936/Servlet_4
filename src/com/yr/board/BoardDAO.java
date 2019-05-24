package com.yr.board;

import java.sql.Connection;
import java.util.List;

import com.yr.page.SearchRow;

public interface BoardDAO {
	//getNum
	public int getNum() throws Exception;
	
	//getTotalCount
	public int getTotalCount(SearchRow searchRow, Connection con) throws Exception;
	
	public BoardDTO selectOne(int num, Connection con) throws Exception;
	
	public List<BoardDTO> selectList(SearchRow searchRow, Connection con) throws Exception;
	
	public int insert(BoardDTO boardDTO, Connection con) throws Exception;
	
	public int update(BoardDTO boardDTO, Connection con) throws Exception;
	
	public int delete(int num, Connection con) throws Exception;
	
}
