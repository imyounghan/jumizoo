/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;


/**
 * 用于跟踪命令的信息
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class TraceInfo {
    private String id;
    private String address;

    public TraceInfo(String traceId, String traceAddress){
        this.id = traceId;
        this.address = traceAddress;
    }

    public String getId(){
        return this.id;
    }

    public String getAddress(){
        return this.address;
    }

//    @Override
//    public int hashCode() {
//        return this.id.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//        else if (!(obj instanceof TraceInfo)) {
//            return false;
//        }
//        else {
//            TraceInfo other = (TraceInfo)obj;
//            return this.id.equals(other.id);
//        }
//    }
}
