package com.tacacs.TacacsPlusServer.utils;

/**
 * 枚举类，Tacacs数据字典
 * @author zengcx
 *
 */
public class TACACS_PLUS {

    private TACACS_PLUS(){}
    
    /** 报文用户认证行为的权限级别,详见Tacacs+协议文档 **/
    public static enum PRIV_LVL {
        MAX(0x0f),ROOT(0x0f),USER(1),MIN(0);

        private final byte code;

        private PRIV_LVL(int code){this.code = (byte) code;}

        public byte code(){
            return code;
        }

    }

    public static final class PACKET{

        private PACKET(){}
        /** 报文中的Version值,详见Tacacs+协议文档,下同 **/
        public static enum VERSION{

            v13_0(0xc0), v13_1(0xc1);

            private final byte code;

            private VERSION(int code){
                this.code = (byte)code;
            }
            public byte code(){return code;}
            public static VERSION forCode(byte b){
                for(VERSION v : values()){
                    if (v.code == b){
                        return v;
                    }
                }
                return null;
            }
        }
        /** 报文TYPE值 **/
        public static enum TYPE{
            /** 新增类型 **/
            START_TLS(0),
            AUTHEN(1),AUTHOR(2),ACCT(3);
            private final byte code;
            private TYPE(int code){
                this.code = (byte) code;
            }
            public byte code(){return code;}
            public static TYPE forCode(byte b) {
                for (TYPE t : values()) { if (t.code==b) { return t; } }
                return null;
            }
        }
        /** 报文FLAG值 **/
        public static enum FLAG {
            UNENCRYPTED(1),SINGLE_CONNECT(4);
            private final byte code;
            private FLAG(int code) { this.code=(byte)code; }
            public byte code() { return code; }
        }
    }
    /** 认证REPLY报文 **/
    public static final class REPLY{

        private REPLY(){}
        public static enum FLAG{
            NOECHO(1),ECHO(0);
            private final byte code;
            private FLAG(int code){
                this.code = (byte)code;
            }
            public byte code(){return code;}
        }
    }
    /** 认证CONTINUE报文 **/
    public static final class CONTINUE{

        private CONTINUE(){}
        public static enum FLAG{
            ABORT(1);
            private final byte code;
            private FLAG(int code){this.code = (byte)code;}
            public byte code(){
                return code;
            }
        }
    }
    /** 认证授权的一些报文信息 **/
    public static final class  AUTHEN{

        private AUTHEN(){}

        /** 认证 START 报文格式 --> action **/
        public static enum ACTION{
            LOGIN(1),CHPASS(2),SENDAUTH(3);
            private final byte code;
            private ACTION(int code){this.code = (byte)code;}
            public static ACTION forCode(byte b) { for (ACTION e : values()) { if (e.code==b) { return e; } } return null; }
            public byte code(){return code;}
        }
        /** 认证 START 报文格式 --> authen_type **/
        public static enum TYPE{
            NOT_SET(0),
            ASCII(1),PAP(2),CHAP(3),ARAP(4),MSCHAP(5),MSCHAPV2(6);
            private final byte code;
            private TYPE(int code){this.code = (byte)code;}
            public byte code(){return code;}
            public static TYPE forCode(byte b) { for (TYPE e : values()) { if (e.code==b) { return e; } } return null; }
        }
        /** 认证 START 报文格式 --> authen_service **/
        public static enum SVC{
            NONE(0),
            LOGIN(1),
            ENABLE(2),
            PPP(3),ARAP(4),PT(5),RCMD(6),X25(7),NASI(8),FWPROXY(9);
            private final byte code;
            private SVC(int code){this.code = (byte)code;}
            public byte code(){return code;}
            public static SVC forCode(byte b) { for (SVC e : values()) { if (e.code==b) { return e; } } return null; }
        }
        /** 认证REQUEST报文格式 --> authen_method **/
        public static enum METH {
            NOT_SET(0),NONE(1),KRB5(2),LINE(3),ENABLE(4),LOCAL(5),TACACSPLUS(6),GUEST(8),RADIUS(0x10),KRB4(0x11),RCMD(0x20);
            private METH(int code) { this.code=(byte)code; }
            private final byte code;
            public byte code() { return code; }
            public static METH forCode(byte b) { for (METH e : values()) { if (e.code==b) { return e; } } return null; }
        }
        /** 认证REPLY报文 --> STATUS **/
        public static enum STATUS {
            PASS(1),FAIL(2),GETDATA(3),GETUSER(4),GETPASS(5),RESTART(6),ERROR(7),FOLLOW(0x21);
            private STATUS(int code) { this.code=(byte)code; }
            private final byte code;
            public byte code() { return code; }
            public static STATUS forCode(byte b) { for (STATUS e : values()) { if (e.code==b) { return e; } } return null; }
        }
    }

    public static final class AUTHOR
    {
        private AUTHOR() {}
        public static enum STATUS
        {
            PASS_ADD(1),PASS_REPL(2),FAIL(0x10),ERROR(0x11),FOLLOW(0x21);
            private STATUS(int code) { this.code=(byte)code; }
            private final byte code;
            public byte code() { return code; }
            public static STATUS forCode(byte b) { for (STATUS e : values()) { if (e.code==b) { return e; } } return null; }
        }
    }

    /** 审计报文信息 **/
    public static final class ACCT{
        private ACCT() {}
        /** 审计REPLY报文格式 --> flags **/
        public static enum FLAG
        {
            START(2),STOP(4),WATCHDOG(8);
            private final byte code;
            private FLAG(int code) { this.code=(byte)code; }
            public byte code() { return code; }
        }
        /** 审计REPLY报文格式 --> status **/
        public static enum STATUS
        {
            SUCCESS(1),ERROR(2),FOLLOW(0x21);
            private STATUS(int code) { this.code=(byte)code; }
            private final byte code;
            public byte code() { return code; }
            public static STATUS forCode(byte b) { for (STATUS e : values()) { if (e.code==b) { return e; } } return null; }
        }
    }

}
