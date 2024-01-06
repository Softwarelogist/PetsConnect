package com.taas.petsconnect.Model;

import java.util.ArrayList;

public class StoryModel {
   private  String storyBy;
   private  long storyAt;

   public String getUserID() {
      return UserID;
   }

   public void setUserID(String userID) {
      UserID = userID;
   }

   private String UserID;
   ArrayList<UserStories>stories;

   public StoryModel() {
   }

   public String getStoryBy() {
      return storyBy;
   }

   public void setStoryBy(String storyBy) {
      this.storyBy = storyBy;
   }

   public long getStoryAt() {
      return storyAt;
   }

   public void setStoryAt(long storyAt) {
      this.storyAt = storyAt;
   }

   public ArrayList<UserStories> getStories() {
      return stories;
   }

   public void setStories(ArrayList<UserStories> stories) {
      this.stories = stories;
   }
}

