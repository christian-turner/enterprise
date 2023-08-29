package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import database.FilmDAO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import models.FilmList;
import models.Film;
//URl

@WebServlet("/filmApi")
public class filmApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	FilmDAO dao = FilmDAO.getInstance();
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		// Calling the DAO for database interaction
		//Obtaining the content type to output the correct data
		String con__type = request.getHeader("Content-Type");
		// getting  the parameters
		String id = request.getParameter("id");
		String title = request.getParameter("title");
		// IF ID IS SUCCESFULLY PASSED
		if (request.getParameterMap().containsKey("id")) {
			// Getting an array of data , based on the parameter passed
			ArrayList<Film> data = dao.readSQL("id" , id);
			// Obtaining ready data to output , from a function that prepares it
			String finalOutput = dao.finalData(data , con__type);
			out.print(finalOutput);		
		}
		// IF TITLE IS PASSED
		if (request.getParameterMap().containsKey("title")) {
			ArrayList<Film> films1 = dao.readSQL("title" , title);
			String finalOutput = dao.finalData(films1 , con__type);
			out.print(finalOutput);		
		}		
		// IF YEAR
		if (request.getParameterMap().containsKey("year")) {
			ArrayList<Film> films = dao.readSQL("year" , request.getParameter("year"));
			String finalOutput = dao.finalData(films , con__type);
			out.print(finalOutput);		
		}
		// IF NOTHING IS PASSSED EG. RETURN ALL DATA
		else if (!request.getParameterMap().containsKey("id") && !request.getParameterMap().containsKey("title")) {
			ArrayList<Film> films = dao.readSQL("all" , "");
			String finalOutput = dao.finalData(films, con__type);
			out.print(finalOutput);
		}	
		
		out.close();	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		// handling the request body with function
	    String postdata = dao.handleRequestBody(request);    
	    // creating an object out of the sent data
	    Film f = dao.formatInwardData(postdata, request.getHeader("Content-Type"));
	    try {
	    	// inserting the film
	    	dao.cudSQL(f , "create");
			out.print("Film added");
		} catch (SQLException e) {
			e.printStackTrace();
			 out.print("Film not added");
		}  	    
	} 
				
	// Same as post but put to insert the data received
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
	    String putData = dao.handleRequestBody(request)  ;
	    Film f = dao.formatInwardData(putData, request.getHeader("Content-Type"));
	    try {
	    	dao.cudSQL(f , "update");
	    	out.print("Film edited");
		} catch (Exception e) {
			e.printStackTrace();
			 out.print("Film edit failed");
		}	
	}
	
	// same as previous two but to remove film from database , shows functions are completely dynamic
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String deleteData = dao.handleRequestBody(request)  ;
	    Film f = dao.formatInwardData(deleteData, request.getHeader("Content-Type"));
		try {
			dao.cudSQL(f , "delete");
			out.write("Film deleted");

		} catch (SQLException e) {
			out.write("Film not deleted - error");
			e.printStackTrace();
		}

	}

}
