package com.yang.eric.a17010.beans;

import com.yang.eric.a17010.greendao.gen.DaoSession;
import com.yang.eric.a17010.greendao.gen.TreeNodeDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by Yang on 2017/5/12.
 */
@Entity
public class TreeNode {

    //自身id
    @Id(autoincrement = true)
    private Long id;
    //外键关联id 父节点id
    private Long pid;
    private int level;
    private int type;



    private String name;
    @ToMany(referencedJoinProperty = "pid")//指定与之关联的其他类的id
    private List<TreeNode> children;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 2127305444)
    private transient TreeNodeDao myDao;
    @Generated(hash = 1916188072)
    public TreeNode(Long id, Long pid, int level, int type, String name) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.type = type;
        this.name = name;
    }
    @Generated(hash = 440943504)
    public TreeNode() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getPid() {
        return this.pid;
    }
    public void setPid(Long pid) {
        this.pid = pid;
    }
    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 807015408)
    public List<TreeNode> getChildren() {
        if (children == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TreeNodeDao targetDao = daoSession.getTreeNodeDao();
            List<TreeNode> childrenNew = targetDao._queryTreeNode_Children(id);
            synchronized (this) {
                if (children == null) {
                    children = childrenNew;
                }
            }
        }
        return children;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1590975152)
    public synchronized void resetChildren() {
        children = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 575492216)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTreeNodeDao() : null;
    }
}
