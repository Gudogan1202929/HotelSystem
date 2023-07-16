package com.example.amnhotelsystem;

import android.content.Context;
import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VolleyAccessRapidAPI {
    private RequestQueue requestQueue;

    public VolleyAccessRapidAPI(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void performLocationSearch(String location, final LocationSearchListener listener) {
        String url = "https://hotels4.p.rapidapi.com/locations/v2/search?query=" + location + "&locale=en_US&currency=USD";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray searchResultsArray = response.getJSONArray("suggestions");
                        if (searchResultsArray.length() > 0) {
                            JSONObject searchResult = searchResultsArray.getJSONObject(0);
                            JSONArray entityArray = searchResult.getJSONArray("entities");
                            JSONObject entityObj = (JSONObject) entityArray.get(0);
                            String geolocationId = (String) entityObj.get("geoId");
                            listener.onLocationSearchSuccess(geolocationId);
                        } else {
                            listener.onLocationSearchError("No search results found.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onLocationSearchError("Failed to parse search results.");
                    }
                },
                error -> {
                    error.printStackTrace();
                    listener.onLocationSearchError("Location search request failed.");
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-RapidAPI-Key", "4e92b210b5msh2266643fc5de797p113beejsnd50e1a09434f");
                headers.put("X-RapidAPI-Host", "hotels4.p.rapidapi.com");
                return headers;
            }
        };

        requestQueue.add(request);
    }


    public void fetchHotelImages(String hotelId, final HotelImagesListener listener) {
        String url = "https://hotels4.p.rapidapi.com/properties/v2/detail";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("currency", "USD");
            jsonBody.put("eapid", 1);
            jsonBody.put("locale", "en_US");
            jsonBody.put("siteId", 300000001);
            jsonBody.put("propertyId", hotelId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        JSONObject hotelsJsonObject = response.getJSONObject("data");
                        JSONObject propertyInfoObject = hotelsJsonObject.getJSONObject("propertyInfo");
                        JSONObject propertyGalleryObject = propertyInfoObject.getJSONObject("propertyGallery");
                        JSONArray imagesArray = propertyGalleryObject.getJSONArray("images");

                        List<ImageHotel> imageList = new ArrayList<>();
                        for (int i = 0; i < imagesArray.length(); i++) {
                            JSONObject imageObject = imagesArray.getJSONObject(i);
                            JSONObject insideImageObject = imageObject.getJSONObject("image");
                            String imageURL = insideImageObject.getString("url");

                            ImageHotel finalImage = new ImageHotel(imageURL);
                            imageList.add(finalImage);
                        }

                        listener.onHotelImagesSuccess(imageList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onHotelImagesError("Failed to parse hotel images response.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        listener.onHotelImagesError("Hotel images request failed.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type", "application/json");
                headers.put("X-RapidAPI-Key", "4e92b210b5msh2266643fc5de797p113beejsnd50e1a09434f");
                headers.put("X-RapidAPI-Host", "hotels4.p.rapidapi.com");
                return headers;
            }
        };

        requestQueue.add(request);
    }

    public void performHotelSearch(String geolocationId, int numOfAdults, int checkInDay, int checkInMonth, int checkInYear, int checkOutDay, int checkOutMonth, int checkOutYear, final HotelSearchListener listener) {
        String url = "https://hotels4.p.rapidapi.com/properties/v2/list";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("currency", "USD");
            jsonBody.put("eapid", 1);
            jsonBody.put("locale", "en_US");
            jsonBody.put("siteId", 300000001);

            JSONObject destinationObject = new JSONObject();
            destinationObject.put("regionId", geolocationId);
            jsonBody.put("destination", destinationObject);

            JSONObject checkInDateObject = new JSONObject();
            checkInDateObject.put("day", checkInDay);
            checkInDateObject.put("month", checkInMonth);
            checkInDateObject.put("year", checkInYear);
            jsonBody.put("checkInDate", checkInDateObject);

            JSONObject checkOutDateObject = new JSONObject();
            checkOutDateObject.put("day", checkOutDay);
            checkOutDateObject.put("month", checkOutMonth);
            checkOutDateObject.put("year", checkOutYear);
            jsonBody.put("checkOutDate", checkOutDateObject);

            JSONArray roomsArray = new JSONArray();
            JSONObject roomObject = new JSONObject();
            roomObject.put("adults", numOfAdults);

            JSONArray childrenArray = new JSONArray();
            JSONObject child1Object = new JSONObject();
            child1Object.put("age", 5);
            JSONObject child2Object = new JSONObject();
            child2Object.put("age", 7);

            childrenArray.put(child1Object);
            childrenArray.put(child2Object);
            roomObject.put("children", childrenArray);

            roomsArray.put(roomObject);
            jsonBody.put("rooms", roomsArray);

            jsonBody.put("resultsStartingIndex", 0);
            jsonBody.put("resultsSize", 200);
            jsonBody.put("sort", "PRICE_LOW_TO_HIGH");

            JSONObject filtersObject = new JSONObject();
            JSONObject priceObject = new JSONObject();
            priceObject.put("max", 150);
            priceObject.put("min", 100);
            filtersObject.put("price", priceObject);
            jsonBody.put("filters", filtersObject);
            String data = "{\n" +
                    "    \"currency\": \"USD\",\n" +
                    "    \"eapid\": 1,\n" +
                    "    \"locale\": \"en_US\",\n" +
                    "    \"siteId\": 300000001,\n" +
                    "    \"destination\": {\n" +
                    "        \"regionId\": \"6054439\"\n" +
                    "    },\n" +
                    "    \"checkInDate\": {\n" +
                    "        \"day\": 10,\n" +
                    "        \"month\": 10,\n" +
                    "        \"year\": 2022\n" +
                    "    },\n" +
                    "    \"checkOutDate\": {\n" +
                    "        \"day\": 15,\n" +
                    "        \"month\": 10,\n" +
                    "        \"year\": 2022\n" +
                    "    },\n" +
                    "    \"rooms\": [\n" +
                    "        {\n" +
                    "            \"adults\": 2,\n" +
                    "            \"children\": [\n" +
                    "                {\n" +
                    "                    \"age\": 5\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"age\": 7\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"resultsStartingIndex\": 0,\n" +
                    "    \"resultsSize\": 200,\n" +
                    "    \"sort\": \"PRICE_LOW_TO_HIGH\",\n" +
                    "    \"filters\": {\n" +
                    "        \"price\": {\n" +
                    "            \"max\": 150,\n" +
                    "            \"min\": 100\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";
            JSONObject jsonObject = new JSONObject(data);
            new Handler().postDelayed(() -> {

            },5000);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        try {
                            JSONObject hotelsJsonObject = response.optJSONObject("data");
                            if (hotelsJsonObject != null && hotelsJsonObject.length() > 0) {
                                JSONObject propertySearchObject = hotelsJsonObject.getJSONObject("propertySearch");
                                JSONArray propertiesArray = propertySearchObject.getJSONArray("properties");

                                List<Hotel> hotelList = new ArrayList<>();
                                for (int i = 0; i < propertiesArray.length(); i++) {
                                    JSONObject propertyObject = propertiesArray.getJSONObject(i);
                                    String hotelId = propertyObject.getString("id");
                                    String hotelName = propertyObject.getString("name");
                                    JSONObject neighborhoodObject = propertyObject.getJSONObject("neighborhood");
                                    String hotelRegion = neighborhoodObject.getString("name");
                                    String leadFormattedPrice = "";
                                    JSONObject priceObject1 = propertyObject.getJSONObject("price");
                                    JSONObject leadObject = priceObject1.optJSONObject("lead");
                                    if (leadObject != null) {
                                        leadFormattedPrice = leadObject.optString("formatted", "");
                                    }
                                    int numberOfDays = calculateStayDuration(checkInYear, checkInMonth, checkInDay, checkOutYear, checkOutMonth, checkOutDay);

                                    JSONObject reviewsObject = propertyObject.getJSONObject("reviews");
                                    int totalReviewCount = reviewsObject.optInt("total", 0);
                                    int reviewScore = reviewsObject.optInt("score", 0);
                                    String imageUrl = "";
                                    JSONObject propertyImageObject = propertyObject.optJSONObject("propertyImage");
                                    JSONObject imageObject = propertyImageObject.optJSONObject("image");
                                    if (imageObject != null) {
                                        imageUrl = imageObject.optString("url", "");
                                    }

                                    Hotel hotel = new Hotel(hotelId, hotelName, hotelRegion, leadFormattedPrice, numberOfDays, reviewScore, totalReviewCount, imageUrl);
                                    hotelList.add(hotel);
                                }

                                listener.onHotelSearchSuccess(hotelList);
                            } else {
                                listener.onHotelSearchError("No hotels found.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onHotelSearchError("Failed to parse hotel search response.");
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        listener.onHotelSearchError("Hotel search request failed.");
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("content-type", "application/json");
                    headers.put("X-RapidAPI-Key", "4e92b210b5msh2266643fc5de797p113beejsnd50e1a09434f");
                    headers.put("X-RapidAPI-Host", "hotels4.p.rapidapi.com");
                    return headers;
                }
            };

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private float calculateTotalPrice(float dailyPrice, int checkInYear, int checkInMonth, int checkInDay, int checkOutYear, int checkOutMonth, int checkOutDay) {
        Calendar checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(checkInYear, checkInMonth, checkInDay);

        Calendar checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.set(checkOutYear, checkOutMonth, checkOutDay);

        int stayDuration = (int) ((checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
        return dailyPrice * stayDuration;
    }

    private int calculateStayDuration(int checkInYear, int checkInMonth, int checkInDay, int checkOutYear, int checkOutMonth, int checkOutDay) {
        Calendar checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(checkInYear, checkInMonth, checkInDay);

        Calendar checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.set(checkOutYear, checkOutMonth, checkOutDay);

        return (int) ((checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis()) / (1000 * 60 * 60 * 24));
    }

    public interface LocationSearchListener {
        void onLocationSearchSuccess(String geolocationId);

        void onLocationSearchError(String message);
    }

    public interface HotelImagesListener {
        void onHotelImagesSuccess(List<ImageHotel> imageList);

        void onHotelImagesError(String message);
    }

    public interface HotelSearchListener {
        void onHotelSearchSuccess(List<Hotel> hotelList);

        void onHotelSearchError(String message);
    }
}