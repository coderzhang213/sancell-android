package cn.sancell.xingqiu.homepage.bean;

import java.util.List;

import cn.sancell.xingqiu.base.base.ListBaseBean;

/**
 * Created by ai11 on 2019/9/8.
 */

public class SeckillSessionListBean extends ListBaseBean {
    private List<SeckillSessionBean> dataList;

    public List<SeckillSessionBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<SeckillSessionBean> dataList) {
        this.dataList = dataList;
    }

    public class SeckillSessionBean {
        private int id;
        public int seckillRoundStatus;
        private String seckillRoundStatusStr;
        private String actBeginTime;
        private long gapTime;
        private String title;
        private String actBeginTimeHourStr;
        private String hour;

        public String getActBeginTime() {
            return actBeginTime;
        }

        public void setActBeginTime(String actBeginTime) {
            this.actBeginTime = actBeginTime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSeckillRoundStatus() {
            return seckillRoundStatus;
        }

        public void setSeckillRoundStatus(int seckillRoundStatus) {
            this.seckillRoundStatus = seckillRoundStatus;
        }

        public String getSeckillRoundStatusStr() {
            return seckillRoundStatusStr;
        }

        public void setSeckillRoundStatusStr(String seckillRoundStatusStr) {
            this.seckillRoundStatusStr = seckillRoundStatusStr;
        }

        public long getGapTime() {
            return gapTime;
        }

        public void setGapTime(long gapTime) {
            this.gapTime = gapTime;
        }

        public String getActBeginTimeHourStr() {
            return actBeginTimeHourStr;
        }

        public void setActBeginTimeHourStr(String actBeginTimeHourStr) {
            this.actBeginTimeHourStr = actBeginTimeHourStr;
        }

        public String getHour() {
            return hour;
        }

        public void setHour(String hour) {
            this.hour = hour;
        }
    }
}
