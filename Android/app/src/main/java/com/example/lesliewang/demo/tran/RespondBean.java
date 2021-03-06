package com.example.lesliewang.demo.tran;

import java.util.List;
/*
使用postman调式接口，试一下接口，主要是为了拿到JSON
使用高能gsonformat插件自动生成java实体类
 */
public class RespondBean {


    /**
     * from : zh
     * to : en
     * trans_result : [{"src":"你好","dst":"Hello"}]
     */

    private String from;
    private String to;
    private List<TransResultBean> trans_result;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<TransResultBean> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResultBean> trans_result) {
        this.trans_result = trans_result;
    }

    public static class TransResultBean {
        /**
         * src : 你好
         * dst : Hello
         */

        private String src;
        private String dst;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}

