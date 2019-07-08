package rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Step1: create a DB connection
		DBConnection connection = DBConnectionFactory.getConnection();
		
		try {
			// Step 2: read user input as a json object from request
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");
			String firstname = input.getString("first_name");
			String lastname = input.getString("last_name");
			
			JSONObject obj = new JSONObject();
			// Step3: run gesiterUser based on user request and return the status result
			if(connection.registerUser(userId, password, firstname, lastname)) {
				obj.put("status", "OK");
			} else {
				// TODO: This should be fixed! false does not always means users already exists
				obj.put("status", "User Already Exists"); 
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
