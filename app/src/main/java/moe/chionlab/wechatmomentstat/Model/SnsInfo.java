package moe.chionlab.wechatmomentstat.Model;

import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.util.LogUtil;

/**
 * 微信朋友圈数据
 * <p/>
 * Created by chiontang on 2/8/16.
 */
public class SnsInfo {
    public String id = "";
    public String authorName = "";
    public String content = "";
    public String authorId = "";
    public String wxnum = "";
    public ArrayList<Like> likes = new ArrayList<Like>();
    public ArrayList<Comment> comments = new ArrayList<Comment>();
    public ArrayList<String> mediaList = new ArrayList<String>();
    public String rawXML = "";
    public long timestamp = 0;
    public boolean ready = false;
    public boolean isCurrentUser = false;
    public boolean selected = true;

    public void print() {
        LogUtil.e("================================");
        LogUtil.e("id: " + this.id);
        LogUtil.e("Author: " + this.authorName);
        LogUtil.e("AuthorId: " + this.authorId);
        LogUtil.e("wxnum: " + this.wxnum);
        LogUtil.e("Content: " + this.content);
        LogUtil.e("Likes:");
        for (int i = 0; i < likes.size(); i++) {
            LogUtil.e(likes.get(i).userName);
        }
        LogUtil.e("Comments:");
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            LogUtil.e("CommentAuthor: " + comment.authorName + "; CommentContent: " + comment.content + "; ToUser: " + comment.toUser);
        }
        LogUtil.e("Media List:");
        for (int i = 0; i < mediaList.size(); i++) {
            LogUtil.e(mediaList.get(i));
        }
    }

    public SnsInfo clone() {
        SnsInfo newSns = new SnsInfo();
        newSns.id = this.id;
        newSns.authorName = this.authorName;
        newSns.content = this.content;
        newSns.authorId = this.authorId;
        newSns.wxnum = this.wxnum;
        newSns.likes = new ArrayList<Like>(this.likes);
        newSns.comments = new ArrayList<Comment>(this.comments);
        newSns.mediaList = new ArrayList<String>(this.mediaList);
        newSns.rawXML = this.rawXML;
        newSns.timestamp = this.timestamp;
        return newSns;
    }

    public void clear() {
        id = "";
        authorName = "";
        content = "";
        authorId = "";
        wxnum = "";
        likes.clear();
        comments.clear();
        mediaList.clear();
        rawXML = "";
    }

    static public class Like {
        public String userName;
        public String userId;
        public boolean isCurrentUser = false;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public boolean isCurrentUser() {
            return isCurrentUser;
        }

        public void setCurrentUser(boolean currentUser) {
            isCurrentUser = currentUser;
        }
    }

    static public class Comment {
        public String authorName;
        public String content;
        public String toUser;
        public String authorId;
        public String wxnum;
        public String toUserId;
        public boolean isCurrentUser = false;

        public String getAuthorName() {
            return authorName;
        }

        public void setAuthorName(String authorName) {
            this.authorName = authorName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getToUser() {
            return toUser;
        }

        public void setToUser(String toUser) {
            this.toUser = toUser;
        }

        public String getAuthorId() {
            return authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

        public String getWxnum() {
            return wxnum;
        }

        public void setWxnum(String wxnum) {
            this.wxnum = wxnum;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public boolean isCurrentUser() {
            return isCurrentUser;
        }

        public void setCurrentUser(boolean currentUser) {
            isCurrentUser = currentUser;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getWxnum() {
        return wxnum;
    }

    public void setWxnum(String wxnum) {
        this.wxnum = wxnum;
    }

    public ArrayList<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getMediaList() {
        return mediaList;
    }

    public void setMediaList(ArrayList<String> mediaList) {
        this.mediaList = mediaList;
    }

    public String getRawXML() {
        return rawXML;
    }

    public void setRawXML(String rawXML) {
        this.rawXML = rawXML;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isCurrentUser() {
        return isCurrentUser;
    }

    public void setCurrentUser(boolean currentUser) {
        isCurrentUser = currentUser;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "{" +
                "wxnum='" + wxnum + '\'' +
                ", authorId='" + authorId + '\'' +
                ", content='" + content + '\'' +
                ", authorName='" + authorName + '\'' +
                ", id='" + id + '\'' +
                ", mediaList=" + mediaList +
                ", timestamp=" + timestamp +
                ", isCurrentUser=" + isCurrentUser +
                '}';
    }
}
