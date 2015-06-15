package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by isaacsiegel on 4/29/15.
 */
@ParseClassName("Submission")
public class Submission extends ParseObject {

    public Submission(){

    }

    public void setPicture(ParseFile pic){
        put("picture", pic);
    }

    public void setThumbnail(ParseFile pic){
        put("thumbnail", pic);
    }

    public ParseFile getPicture(){
        return getParseFile("picture");
    }

    public ParseFile getThumbnail(){
        return getParseFile("thumbnail");
    }

    public Date getCreatedDate() { return getCreatedAt(); }

    public String getPhotoURL() {
        if(getPicture() == null || getPicture().getUrl() ==null)
            return "https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg";
        else
            return getPicture().getUrl();
    }

    public String getThumbnailURL() {
        if(getThumbnail() == null || getThumbnail().getUrl() ==null)
            return "https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg";
        else
            return getThumbnail().getUrl();
    }

    public void setCreator(ParseUser value) {
        put("creator", value);
    }

    public ParseUser getCreator() { return  getParseUser("creator"); }

    public String getCreatorUsername() { return getParseUser("creator").getUsername(); }

    public boolean getIsValid(){
        return getBoolean("isValid");
    }

    public void setIsValid(boolean isValid){
        put("isValid", isValid);
    }

    public boolean getHasProcessed(){
        return getBoolean("hasProcessed");
    }

    public void setHasProcessed(boolean hasProcessed){
        put("hasProcessed", hasProcessed);
    }

}
