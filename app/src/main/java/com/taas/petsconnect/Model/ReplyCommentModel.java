package com.taas.petsconnect.Model;

public class ReplyCommentModel {
    private String replycommmentBody;
    private long replycommentAt;
    private String replycommentBy;



    public int getReplycommentLike() {
        return replycommentLike;
    }

    public void setReplycommentLike(int replycommentLike) {
        this.replycommentLike = replycommentLike;
    }

    private int replycommentLike;

    public String getReplyCommentId() {
        return ReplyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        ReplyCommentId = replyCommentId;
    }

    private String ReplyCommentId;

    public int getLikecomment() {
        return likecomment;
    }

    public void setLikecomment(int likecomment) {
        this.likecomment = likecomment;
    }

    private int likecomment;
    public ReplyCommentModel(String replycommmentBody, long replycommentAt, String replycommentBy) {
        this.replycommmentBody = replycommmentBody;
        this.replycommentAt = replycommentAt;
        this.replycommentBy = replycommentBy;
    }

    public ReplyCommentModel() {
    }

    public String getReplycommmentBody() {
        return replycommmentBody;
    }

    public void setReplycommmentBody(String replycommmentBody) {
        this.replycommmentBody = replycommmentBody;
    }

    public long getReplycommentAt() {
        return replycommentAt;
    }

    public void setReplycommentAt(long replycommentAt) {
        this.replycommentAt = replycommentAt;
    }

    public String getReplycommentBy() {
        return replycommentBy;
    }

    public void setReplycommentBy(String replycommentBy) {
        this.replycommentBy = replycommentBy;
    }
}
