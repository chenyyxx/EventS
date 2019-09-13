package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";  // host + end point
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "9u5pZtaFsJtlxZeDAyGeeMawMlIAQ32B";

	public List<Item> search(double lat, double lon, String keyword) {
//		List<Item> result = new ArrayList<>();
		
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // Making your input keyword into a url acceptable form: rick sun => rick%20sun 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// %s is a space holder, and will be replaced by the later specifying parameters
		// These parameters can be viewed on the site supported API list
		// build 	a query based on the input
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		String url = URL + "?" + query; // create a query url based on input information
		// FULL URL:  "https://app.ticketmaster.com/discovery/v2/events.json?apikey=qqPuP6n3ivMUoT9fPgLepkRMreBcbrjV&latlong=37,-120&keyword=event&radius=50"
		try {
			// try to reate an url connection and send a GET http request using the url
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET"); // Specify request type
			
			int responseCode = connection.getResponseCode(); // Send a response code
			System.out.println("Sending request to url:" + url);
			System.out.println("Response code:" + responseCode);
			
			if (responseCode != 200) {
				return new ArrayList<>(); // handle the case where response code != 200
			}
			
			// Read a set of string from strings from memory for CPU process
			// Avoid repeated IOI
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			
			while((line = reader.readLine())!=null) {
				response.append(line);
			}
			reader.close();
			// Turn the response content into JSONObject type
			JSONObject obj = new JSONObject(response.toString());
			
			// check if response contains the key called "_embedded"
			if(!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList();
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		for(int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			
			Item.ItemBuilder builder = new ItemBuilder();
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if(!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if(!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			builder.setAddress(getAddress(event))
			.setCategories(getCategories(event))
			.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	// Helper Methods to get the address information from API
	private String getAddress(JSONObject event) throws JSONException{
		if(!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if(!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for(int i=0; i<venues.length();i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if(!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if(!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}
						if(!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}
						if(!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
						
					}
					if(venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}
					
					String result = builder.toString();
					if(!result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return "";
	}
	
	/**
	 * Helper Methods to fetch the imageURL from API
	 */
	private String getImageUrl(JSONObject event) throws JSONException {
		if(!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for(int i =0; i<array.length();i++) {
				JSONObject image = array.getJSONObject(i);
				if(!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}
	
	/**
	 * Helper Method to fetch the categories from API
	 */
	private Set<String> getCategories(JSONObject event) throws JSONException{
		// TODO This has some problems
		Set<String> categories = new HashSet<>();
		if(!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for(int i = 0; i<classifications.length();i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if(!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if(!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	// A test method to print result for debugging
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		
		try {
			for(Item event: events) {
				System.out.println(event.toJSONObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX

		tmApi.queryAPI(29.682684, -95.295410);

	}

}
