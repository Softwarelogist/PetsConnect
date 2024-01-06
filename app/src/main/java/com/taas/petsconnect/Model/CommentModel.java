package com.taas.petsconnect.Model;

public class CommentModel {
    private String commmentBody;
    private long commentAt;
    private String commentBy;

    private String commentId;

    public int getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(int commentLike) {
        this.commentLike = commentLike;
    }

    public CommentModel(String commmentBody, long commentAt, String commentBy, String commentId, int commentLike) {
        this.commmentBody = commmentBody;
        this.commentAt = commentAt;
        this.commentBy = commentBy;
        this.commentId = commentId;
        this.commentLike = commentLike;
    }

    private int commentLike;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }


    public CommentModel() {
    }

    public CommentModel(String commmentBody, long commentAt, String commentBy) {
        this.commmentBody = commmentBody;
        this.commentAt = commentAt;
        this.commentBy = commentBy;
    }

    public String getCommmentBody() {
        return commmentBody;
    }

    public void setCommmentBody(String commmentBody) {
        this.commmentBody = commmentBody;
    }

    public long getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(long commentAt) {
        this.commentAt = commentAt;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }


}
