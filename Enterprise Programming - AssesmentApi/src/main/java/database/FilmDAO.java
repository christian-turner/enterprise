package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import models.Film;
import models.FilmList;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.*;


public class FilmDAO {
	
	private static FilmDAO instance ;
	private FilmDAO() {}
	
	public static FilmDAO getInstance() {
		if (instance == null) {
			instance = new FilmDAO();
		}
		return instance;
	}
	
	String data = "";
	Film oneFilm = null;
	Connection conn = null;
    Statement stmt = null;
    
	String user = "turnerch";
    String password = "Mathgerp5";
    String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/"+user;
    
  //  String user = "enterprise";
  //  String password = "Christian10";

   // String url =
    	//	"jdbc:mysql://database-1.cprgofkgdyby.eu-west-2.rds.amazonaws.com:3306/enterprise1?user="+user+"&password=" + password;

	

	
	private void openConnection(){
		// loading jdbc driver for mysql
		try{
		    Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch(Exception e) { System.out.println(e); }

		// connecting to database
		try{
			// connection string for demos database, username demos, password demos
 			conn = DriverManager.getConnection(url, user, password);
			//conn = DriverManager.getConnection(url);
		    stmt = conn.createStatement();
		} catch(SQLException se) { System.out.println(se); }	   
    }
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Film getNextFilm(ResultSet rs){
    	Film thisFilm=null;
		try {
			thisFilm = new Film(
					rs.getInt("id"),
					rs.getString("title"),
					rs.getInt("year"),
					rs.getString("director"),
					rs.getString("stars"),
					rs.getString("review"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisFilm;		
	}
	
 
 // This function deals with the read operations , returning data from the db to the user
 public ArrayList<Film> readSQL(String parameter , String value){
	 ArrayList<Film> matchedFilms =new ArrayList<Film>();
	 openConnection();
	 String SQL = "";
	 // checking the parameter to see what sql should be populated
	 if (parameter == "all") {
		 SQL = "select * from films"; 
	 }
	 else if (parameter == "id") {
		 int id2 = Integer.parseInt(value);
		 SQL = "select * from films where id="+id2;
	 }
	 else if (parameter == "title") {
		 SQL = "SELECT * FROM films\n"
			 		+ "WHERE title LIKE '%"+value+"'";
	 }
	 else if (parameter == "year") {
		 SQL = "select * from films where year = " + value;
	 }
	 
	try{
	    ResultSet rs1 = stmt.executeQuery(SQL);
	    // iterating through the results and appending to a array list 
	    while(rs1.next()){
	    	oneFilm = getNextFilm(rs1);
		   	matchedFilms.add(oneFilm);
		  }

	    stmt.close();
	    closeConnection();
	    } 
	
	catch(SQLException se) { 
	    	System.out.println(se); 
	    	}
	return matchedFilms;
	 
	 
 }
 
// this is a function for the sql of connection types which handle incoming data. 
 public void cudSQL (Film f , String operation) throws SQLException {
	
	 boolean b = false;
	 String sql = "";
	 // here we check what the operation is and append the appropriate sql
	 if (operation == "create") {
		 int id = getLastID();
		 sql = "insert into films(id,title,year,director,stars,review)"
		    		+ "values ("+id+", '"+f.getTitle()+"', "+f.getYear()+" ,"
		    				+ " '"+f.getDirector()+"' , '"+f.getStars()+"', '"+f.getReview()+"')";
	 }
	 else if (operation == "update") {
		 sql = "update films"
		   			+ " set title = '"+f.getTitle()+"', year = '"+f.getYear()+"' "
		   					+ ", director = '"+f.getDirector()+"' , stars = '"+f.getStars()+"' , review = '"+f.getReview()
		   						+ "' where id = " + f.getId();
	 }
	 
	 else if (operation == "delete") {
			sql = "delete from films where id = " + f.getId();
	 }
	 try {
		 // executing the sql statement
		 System.out.println(sql);
		 openConnection();
		 b = stmt.execute(sql);
		 closeConnection();
		   
	   } catch (SQLException e) {
		   throw new SQLException("Error sending "+operation+" data");
	   }
	 
 }
  

// function that takes a array list of films and the content type. Turning the array list into data that the user chose
   public String finalData(ArrayList<Film> films , String conType) {	  
	   String finalOutput = "";
	   if (conType.equals("application/json")) {
			String data = this.toJson(films);
			finalOutput = finalOutput + data;
		}
		if (conType.equals("application/xml")) {
			String data = this.toXml(films);
			finalOutput = finalOutput + data;
		} 
		if (conType.equals("application/text")) {
			String data = this.toText(films);
			finalOutput = finalOutput + data;
		}
		
		// returning the films in the currently selected format
		return finalOutput;
	   
   }
   
   // function that turns an array list of films to json data
   public String toJson(ArrayList<Film> films) {
	   Gson gson = new Gson();
	   String json = gson.toJson(films);
	   return json;
   }
   // function that turns an array list of films to xml data
   public String toXml(ArrayList<Film> films) {
	   try {
			FilmList cl = new FilmList(films);
			StringWriter sw = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(FilmList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(cl, sw);
			return (sw.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	return null;
	   
   }
   // function that turns an array list of films to text data
   public String toText (ArrayList<Film> films) {
	   String finalOutput = "";
	   for(Film film : films) {
			String output = (film.getId() +"#" + film.getTitle() +"#" 
		+ film.getYear()+ "#" + film.getDirector() + "#"+  film.getStars() + "#" + film.getReview() + 
		"/n");
			
		finalOutput = finalOutput + output;
   }
	   return finalOutput;
   }
   
  // turning inward data into objects . Completely dynamic so works for post and put.
  public Film formatInwardData(String data, String contentType) {
	  if (contentType.equals("application/json")) {
		  Film f = jsonToFilm(data);
		  return f;
	  }
	  else if (contentType.equals("application/xml")) {
		  Film f = xmlToFilm(data);
		  return f;
	  }
	  
	  if (contentType.equals("application/text")) {
		  Film f = textToFilm(data);
		  return f;
	  }
	  return null;
  }
  
  public Film jsonToFilm(String data) {
	  Gson gson = new Gson();
	  // uses gson to format the json and turn it into a film object 
	  ArrayList<Film> returnFilms = gson.fromJson(data , new TypeToken<List<Film>>(){}.getType());
	  for(Film f: returnFilms) {
			Film film = new Film(f.getId() , f.getTitle() , f.getYear() , f.getDirector() , f.getStars() ,  f.getReview());
			return film;
	  }	  
	  return null;
  }
  
  public Film xmlToFilm(String data) {
	  try {
	  JAXBContext jaxbContext = JAXBContext.newInstance(Film.class);
	  Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
	  Film f = (Film) jaxbUnmarshaller.unmarshal(new StringReader(data));
	  return f;
	  } catch(Exception e) {
		  e.printStackTrace();
		  return null;
	  }
  }
  public Film textToFilm(String data) {
	  // turning the text data into a film object
	  String[] words = data.split("#");
	  System.out.println(words.length);
	  if (words.length== 6) {
		  Film f = new Film(Integer.parseInt(words[0]) , words[1] , Integer.parseInt(words[2]) , words[3] , words[4] , words[5]);
		  return f;
	  }
	  if (words.length== 5) {
		  Film f = new Film(0 , words[0] , Integer.parseInt(words[1]) , words[2] , words[3] , words[4]);
		  return f;
	  }
	  if (words.length == 1) {
		  Film f = new Film();
		  f.setId( Integer.parseInt(words[0]));
		  System.out.println(f.getId());
		  return f;
	  }
	  return null;
  }
//function to get the last id in the database and add one. So that new films are always added last.
public int getLastID() throws SQLException {
	 
	 boolean b = false;
	 try {
		 // sql to retrieve the last id
		 String sql = "SELECT id FROM films ORDER BY ID DESC LIMIT 1";
		 System.out.println(sql);
		 openConnection();
		 ResultSet rs = stmt.executeQuery(sql);
		 while (rs.next()) {
			 // parsing it
			 int id = rs.getInt("id");
			 int id2 = id +=1;
			 System.out.println(id2);
			 return id2;
		 }
		 
		 closeConnection();
		 
	 }catch (SQLException s) {
			throw new SQLException("Error");
		}
	return 0;
}
 // function so that the incoming request body can be read properly
 public String handleRequestBody(HttpServletRequest request) {
	 try {
		 StringBuilder b = new StringBuilder();
		 BufferedReader r = request.getReader();
		 String line;
		 while ((line = r.readLine()) !=  null) {
			 b.append(line);
			 }
		 String postdata = b.toString();  
		 return postdata;
	 
 } catch(Exception e){
	 e.printStackTrace();
 }
	 return null;
 }
 
 
 
}
